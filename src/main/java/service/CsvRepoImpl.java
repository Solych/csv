package service;

import ch.qos.logback.classic.Logger;
import model.Job;
import model.Weights;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import repository.CsvRepo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service for calling function from repository
 *
 * @see repository.CsvRepo
 */
@Service(value = "RepoImpl")
@ComponentScan(basePackageClasses = {repository.CsvRepo.class, config.TransactionConfig.class})
public class CsvRepoImpl {

    private final String PATH = "Z://JavaProject//csv//temp2.csv";


    @Autowired
    private JpaTransactionManager transactionManager;

    @Autowired
    @Qualifier("Repo")
    private CsvRepo csvRepo;

    @Autowired
    private Logger logger;

    @PersistenceContext
    private EntityManager entityManager;

    private int rowNum;


    /**
     * Function for searching any notes from db. If exists - write their in csv and return true, else -
     * returns false
     */
    public boolean isFindAll() throws IOException {
        List<Weights> weights = csvRepo.findAll();
        if (weights.size() != 0) {

            BufferedWriter writer = new BufferedWriter(new FileWriter(PATH));
            CSVPrinter csvPrinter = new CSVPrinter(writer,
                    CSVFormat.DEFAULT.withHeader("word", "value").withDelimiter(','));
            for (Weights weights1 : weights)
                csvPrinter.printRecord(weights1.getWord().trim(), weights1.getStr_value());
            csvPrinter.flush();
            writer.close();
            csvPrinter.close();
            return true;
        }
        return false;

    }


    /**
     * Function for parse excel file from client (xlsx or xls)
     * Create workbook from inputStream file
     * Next we creates a Iterator for bust all lines from file. We have only 2 *fields* in every line
     * And we can get him by index 0 and 1, but if second type of *field* is not numeric that it's a
     * header or invalid line.
     */
    public boolean save(MultipartFile multipartFile) {

        if (multipartFile.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            try {
                XSSFWorkbook wb = new XSSFWorkbook(multipartFile.getInputStream());
                XSSFSheet sheet = wb.getSheetAt(0);
                Iterator<Row> iterator = sheet.iterator();
                while (iterator.hasNext()) {
                    Row rowCurrent = iterator.next();
                    if (rowCurrent.getCell(1).getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
                        csvRepo.save(rowCurrent.getCell(0).getStringCellValue(),
                                new BigDecimal(rowCurrent.getCell(1).getNumericCellValue()));
                }
                return true;

            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        } else if (multipartFile.getContentType().equals("application/vnd.ms-excel")) {
            try {
                HSSFWorkbook wb = new HSSFWorkbook(multipartFile.getInputStream());
                HSSFSheet sheet = wb.getSheetAt(0);
                Iterator<Row> iterator = sheet.iterator();
                while (iterator.hasNext()) {
                    Row currentRow = iterator.next();
                    if (currentRow.getCell(1).getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
                        csvRepo.save(currentRow.getCell(0).getStringCellValue(),
                                new BigDecimal(currentRow.getCell(1).getNumericCellValue()));
                }
                return true;


            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        } else
            return false;
    }


    /**
     * Method for parse file and inserting rows in db by a 5 threads
     * @param file xlsx file from client
     * @throws IOException
     */
    public void createTasks(final MultipartFile file) throws IOException {
        final int NUMBER_THREADS = 5;
        ExecutorService pool = Executors.newFixedThreadPool(NUMBER_THREADS);
        List<Callable<Object>> tasks = new ArrayList<>();

        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        ArrayList<Job> batch = new ArrayList<>();
        iterator.next(); // skip first row cus exists header
        while (iterator.hasNext()) {
            for(int i = 0;i<50;i++) {
                Row tempRow = iterator.next();
                batch.add(new Job((long)tempRow.getCell(0).getNumericCellValue(),
                        tempRow.getCell(1).getStringCellValue(),
                        tempRow.getCell(2).getStringCellValue(),
                        tempRow.getCell(3).getStringCellValue(),
                        tempRow.getCell(4).getStringCellValue()));
            }
            tasks.add(() -> new TransactionTemplate(transactionManager).execute((TransactionStatus status) -> {
                        try {
                            rowNum += 50;
                            saveBatch(batch, rowNum);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        return null;
                    }

            ));

        }
        try {
            logger.debug(""+ tasks.size());
            pool.invokeAll(tasks);
        } catch (InterruptedException ex) {
            logger.debug(ex.getMessage());
            ex.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }


    /**
     * Method for save batch(50) rows in db by one transaction
     * Used native query, cus with method persist fields swapped places.
     * @param weights 50 objects of Job
     * @throws IOException
     */
    @Transactional
    public void saveBatch(ArrayList<Job> weights, int counter) throws IOException {
        for(int i = counter-50;i<counter;i++){
            entityManager.createNativeQuery(
                    "INSERT INTO timetable.job(id, room, date_time, group_name, discipline) VALUES (?,?,?,?,?)")
                    .setParameter(1,weights.get(i).getId())
                    .setParameter(2, weights.get(i).getRoom())
                    .setParameter(3, weights.get(i).getDateTime())
                    .setParameter(4, weights.get(i).getGroupName())
                    .setParameter(5, weights.get(i).getDiscipline())
                    .executeUpdate();
        }
        try {
            entityManager.flush();
        } catch (Throwable ex){
            logger.debug(ex.getMessage());
        }

    }


}
