package service.impl;

import ch.qos.logback.classic.Logger;
import exceptions.EmptyDbException;
import model.Job;
import model.Lines;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import service.FuncInterface;
import service.JobService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service with program logic with insert/select to/from db of timetable lines
 */
@Service(value = "JobServiceImpl")
public class JobServiceImpl implements JobService {


    @Autowired
    private JpaTransactionManager transactionManager;

    @Autowired
    @Qualifier("EntityServiceImpl")
    private EntityServiceImpl entityService;

    @Autowired
    private Logger logger;

    @PersistenceContext
    private EntityManager entityManager;

    private int rowsCount;

    private int recordedRowsCount;
    private final String FIRST_COLUMN = "ID";
    private final String SECOND_COLUMN = "ROOM";
    private final String THIRD_COLUMN = "DATE_TIME";
    private final String FOURTH_COLUMN = "GROUP_NAME";
    private final String FIFTH_COLUMN = "DISCIPLINE";
    private final String SUFFIX = "csv";
    private final String PREFIX = "tempFile";

    private final int BATCH_SIZE = 50;

    private final String XLS_CONTENT_TYPE = "application/vnd.ms-excel";
    private final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";



    private Map<String, FuncInterface<Iterator<Row>, InputStream>> myMap = new TreeMap<>();
    private FuncInterface<Iterator<Row>, InputStream> xlsx = (xlsxStream) -> {
        XSSFWorkbook workbook = new XSSFWorkbook(xlsxStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        return sheet.iterator();

    };

    private FuncInterface<Iterator<Row>, InputStream> xls = (xlsStream) -> {
        HSSFWorkbook workbook = new HSSFWorkbook(xlsStream);
        HSSFSheet sheet = workbook.getSheetAt(0);
        return sheet.iterator();
    };



    /**
     * Function for searching any notes from db. If exists - write their in csv and return true, else -
     * returns false
     */
    public InputStreamResource read() throws EmptyDbException, IOException {

        List<Job> jobs = entityService.findAll();
        if (jobs.size() != 0) {
            File file = File.createTempFile(PREFIX, SUFFIX);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            CSVPrinter csvPrinter = new CSVPrinter(writer,
                    CSVFormat.DEFAULT.withHeader(FIRST_COLUMN, SECOND_COLUMN, THIRD_COLUMN, FOURTH_COLUMN, FIFTH_COLUMN)
                            .withDelimiter(','));
            for (Job job : jobs)
                csvPrinter.printRecord(job.getId(), job.getRoom(),
                        job.getDateTime(), job.getGroupName(), job.getDiscipline());
            csvPrinter.flush();
            writer.close();
            csvPrinter.close();
            InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(file));
            file.deleteOnExit();
            return inputStreamResource;
        }
        throw new EmptyDbException();
    }


    /**
     * Method for parse file and inserting rows in db by a 5 threads
     *
     * @param file xlsx file from client
     * @return number of lines which was writing in db
     * @throws IOException
     */
    public Lines write(final MultipartFile file) throws Exception {

        myMap.put(XLSX_CONTENT_TYPE, xlsx);
        myMap.put(XLS_CONTENT_TYPE, xls);
        if (myMap.containsKey(file.getContentType())) {
            Iterator<Row> iterator =
                    file.getContentType().equals(XLS_CONTENT_TYPE) ?
                    xls.get(file.getInputStream()) : xlsx.get(file.getInputStream());

            final int NUMBER_THREADS = 5;
            ExecutorService pool = Executors.newFixedThreadPool(NUMBER_THREADS);
            List<Callable<Object>> tasks = new ArrayList<>();
            ArrayList<Job> jobsList = new ArrayList<>();
            iterator.next(); // skip first row cus exists header
            while (iterator.hasNext()) {
                for (int i = 0; i < BATCH_SIZE; i++) {
                    Row tempRow = iterator.next();
                    jobsList.add(new Job((long) tempRow.getCell(0).getNumericCellValue(),
                            tempRow.getCell(1).getStringCellValue(),
                            tempRow.getCell(2).getStringCellValue(),
                            tempRow.getCell(3).getStringCellValue(),
                            tempRow.getCell(4).getStringCellValue()));
                }
                tasks.add(() -> new TransactionTemplate(transactionManager).execute((TransactionStatus status) -> {
                            try {
                                rowsCount += BATCH_SIZE;
                                if (saveBatch(jobsList, rowsCount)) {
                                    recordedRowsCount += BATCH_SIZE;
                                }
                            } catch (IOException ex) {
                                logger.debug(ex.getLocalizedMessage());
                                ex.printStackTrace();
                            }
                            return null;
                        }

                ));
            }

            try {
                /*List<Future<Object>> invokeAll =*/
                pool.invokeAll(tasks);
                return new Lines(recordedRowsCount, jobsList.size() - recordedRowsCount);
            } catch (InterruptedException ex) {
                logger.debug(ex.getMessage());
                ex.printStackTrace();
                throw new Exception("Something is wrong");
            } finally {
                pool.shutdown();
            }
        }
        throw new IOException();
    }


    /**
     * Method for save batch(50) rows in db by one transaction
     * Used native query, cus with method persist fields swapped places.
     *
     * @param batch 50 objects of Job
     * @throws IOException
     */
    @Transactional
    private boolean saveBatch(ArrayList<Job> batch, int rowNumber) throws IOException {
        for (int i = rowNumber - BATCH_SIZE; i < rowNumber; i++) {
            entityManager.createNativeQuery(
                    "INSERT INTO timetable.job(id, room, date_time, group_name, discipline) VALUES (?,?,?,?,?)")
                    .setParameter(1, batch.get(i).getId())
                    .setParameter(2, batch.get(i).getRoom())
                    .setParameter(3, batch.get(i).getDateTime())
                    .setParameter(4, batch.get(i).getGroupName())
                    .setParameter(5, batch.get(i).getDiscipline())
                    .executeUpdate();
        }
        try {
            entityManager.flush();
        } catch (Throwable ex) {
            logger.debug(ex.getMessage());
            return false;
        }
        return true;
    }


}
