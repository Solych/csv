package controller;

import exceptions.EmptyDbException;
import model.Lines;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import service.impl.JobServiceImpl;

import java.io.IOException;
import java.util.Locale;


/**
 * Controller for interception of requests:
 * /download: returns a csv file with lines from db if exists
 * /upload: receives a file with extension .xlsx or .xls, parse him and write parsed lines in a bd
 * (if extension is not xlsx/xls - returns bad request)
 *
 * @see JobServiceImpl
 */
@Controller
public class LoadController {

    @Autowired
    @Qualifier("JobServiceImpl")
    private JobServiceImpl jobService;

    private String INVALID_TYPE_FILE;
    private String EMPTY_DB;
    private String INTERNAL_SERVER_ERROR;
    private String DOWNLOAD_IO_EXCEPTION;
    private final static String RU_COUNTRY = "RU";


    @Autowired
    private MessageSource messageSource;


    /**
     * Function recognize if exists any notes in db. If answer on this question - true - returns a stream of csv
     * Else - returns bad request
     */
    @GetMapping("/download")
    public ResponseEntity<?> download(Locale locale) {
        try {
            InputStreamResource resource = jobService.read();
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
        } catch (IOException ex) {
            DOWNLOAD_IO_EXCEPTION = messageSource.getMessage("message.DOWNLOAD_IO_EXCEPTION", null,
                    (locale.getCountry() == RU_COUNTRY) ? new Locale("ru", "RU")
                            : new Locale("en", "US"));
            return new ResponseEntity<>(DOWNLOAD_IO_EXCEPTION, HttpStatus.BAD_REQUEST);
        } catch (EmptyDbException ex) {
            EMPTY_DB = messageSource.getMessage("message.EMPTY_DB", null,
                    (locale.getCountry() == RU_COUNTRY) ? new Locale("ru", "RU")
                            : new Locale("en", "US"));
            return new ResponseEntity<>(EMPTY_DB, HttpStatus.NOT_FOUND);
        }


    }


    /**
     * Method for uploading a file which contains a students timetable with extension xlsx/xls
     *
     * @param file with timetable which have extension xlsx/xls
     * @return httpStatus.ok
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, Locale locale) {

        try {
            Lines lines = jobService.write(file);
            return new ResponseEntity<>(lines, HttpStatus.OK);
        } catch (IOException ex) {
            INVALID_TYPE_FILE = messageSource.getMessage("message.INVALID_TYPE_FILE", null,
                    (locale.getCountry() == RU_COUNTRY) ? new Locale("ru", "RU")
                            : new Locale("en", "US"));
            return new ResponseEntity<>(INVALID_TYPE_FILE, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            INTERNAL_SERVER_ERROR = messageSource.getMessage("message.INTERNAL_SERVER_ERROR", null,
                    (locale.getCountry() == RU_COUNTRY) ? new Locale("ru", "RU")
                            : new Locale("en", "US"));
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


}
