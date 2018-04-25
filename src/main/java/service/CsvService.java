package service;

import ch.qos.logback.classic.Logger;
import exceptions.EmptyDbException;
import model.Job;
import model.Lines;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import service.impl.JobServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service with program logic with insert/select to/from db of timetable lines
 */
@Service(value = "Service")
public class CsvService {


    @Autowired
    private JpaTransactionManager transactionManager;

    @Autowired
    @Qualifier("JobService")
    private JobServiceImpl jobService;

    @Autowired
    private Logger logger;

    @PersistenceContext
    private EntityManager entityManager;

    private int rowNum;

    private int recordedLines;

    private final int BATCH_SIZE = 50;


    /**
     * Function for searching any notes from db. If exists - write their in csv and return true, else -
     * returns false
     */
    public InputStreamResource getAllByStream() throws EmptyDbException, IOException {
        List<Job> jobs = jobService.findAll();
        if (jobs.size() != 0) {
            File file = File.createTempFile("tempFile", "csv");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            CSVPrinter csvPrinter = new CSVPrinter(writer,
                    CSVFormat.DEFAULT.withHeader("ID", "ROOM", "DATE_TIME", "GROUP_NAME", "DISCIPLINE")
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
    public Lines createTasks(final MultipartFile file) throws Exception {
        final int NUMBER_THREADS = 5;
        ExecutorService pool = Executors.newFixedThreadPool(NUMBER_THREADS);
        List<Callable<Object>> tasks = new ArrayList<>();
        ArrayList<Job> batch = new ArrayList<>();

        if (file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                || file.getContentType().equals("application/vnd.ms-excel")) {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();

            iterator.next(); // skip first row cus exists header
            while (iterator.hasNext()) {
                for (int i = 0; i < BATCH_SIZE; i++) {
                    Row tempRow = iterator.next();
                    batch.add(new Job((long) tempRow.getCell(0).getNumericCellValue(),
                            tempRow.getCell(1).getStringCellValue(),
                            tempRow.getCell(2).getStringCellValue(),
                            tempRow.getCell(3).getStringCellValue(),
                            tempRow.getCell(4).getStringCellValue()));
                }
                tasks.add(() -> new TransactionTemplate(transactionManager).execute((TransactionStatus status) -> {
                            try {
                                rowNum += BATCH_SIZE;
                                if (saveBatch(batch, rowNum))
                                    recordedLines += BATCH_SIZE;
                            } catch (IOException ex) {
                                logger.debug(ex.getLocalizedMessage());
                                ex.printStackTrace();
                            }
                            return null;
                        }

                ));
            }

            try {
                logger.debug("" + tasks.size());
                /*List<Future<Object>> invokeAll =*/
                pool.invokeAll(tasks);
                return new Lines(recordedLines, batch.size() - recordedLines);
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
     * @param weights 50 objects of Job
     * @throws IOException
     */
    @Transactional
    private boolean saveBatch(ArrayList<Job> weights, int rowNumber) throws IOException {
        for (int i = rowNumber - BATCH_SIZE; i < rowNumber; i++) {
            entityManager.createNativeQuery(
                    "INSERT INTO timetable.job(id, room, date_time, group_name, discipline) VALUES (?,?,?,?,?)")
                    .setParameter(1, weights.get(i).getId())
                    .setParameter(2, weights.get(i).getRoom())
                    .setParameter(3, weights.get(i).getDateTime())
                    .setParameter(4, weights.get(i).getGroupName())
                    .setParameter(5, weights.get(i).getDiscipline())
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
