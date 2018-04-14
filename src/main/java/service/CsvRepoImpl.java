package service;

import ch.qos.logback.classic.Logger;
import model.Weights;
import org.apache.commons.collections4.iterators.ArrayListIterator;
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
//@Transactional(propagation = Propagation.REQUIRED)
@Service(value = "RepoImpl")
@ComponentScan(basePackageClasses = {repository.CsvRepo.class, config.TransactionConfig.class})
public class CsvRepoImpl {

    final String PATH = "Z://JavaProject//csv//temp2.csv";


    @Autowired
    private JpaTransactionManager transactionManager;

    @Autowired
    @Qualifier("Repo")
    private CsvRepo csvRepo;

    @Autowired
    private Logger logger;

    @PersistenceContext
    EntityManager entityManager;


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


    public void createTasks(final MultipartFile file) throws IOException {
        final int NUMBER_THREADS = 5;
        ExecutorService pool = Executors.newFixedThreadPool(NUMBER_THREADS);
        List<Callable<Object>> tasks = new ArrayList<>();
        ArrayList<Weights> batch = new ArrayList<>();
        ArrayList<Weights> tempBatch = new ArrayList<>();

        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);

        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            final Row tempRow = iterator.next();
            for(int i = 0;i<49;i++) {
                iterator.next();
            }


            tasks.add(() -> new TransactionTemplate(transactionManager).execute((TransactionStatus status) -> {
                        try {
                            saveBatch(file, tempRow);


                            //csvRepo.saveAll(batch);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        return null;
                    }

            ));
        }
        try {
            logger.debug("" + tasks.size());
            pool.invokeAll(tasks);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }

    public void saveBatch(MultipartFile file, Row currentRow) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);

        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()){
            Row tempRow = iterator.next();
            if(tempRow.equals(currentRow))
                break;
        }

        while(iterator.hasNext()) {
            Row tempRow2 = iterator.next();
            entityManager.persist(new Weights(tempRow2.getCell(0).getStringCellValue(),
                    new BigDecimal(tempRow2.getCell(1).getNumericCellValue())));
        }


        //entityManager.close();

    }
}
