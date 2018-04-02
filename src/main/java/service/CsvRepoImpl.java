package service;

import model.Weights;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import repository.CsvRepo;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

/**
 * Service for calling function from repository
 * @see repository.CsvRepo
 */
@Service(value = "RepoImpl")
@ComponentScan(basePackageClasses = repository.CsvRepo.class)
public class CsvRepoImpl {


    @Autowired
    @Qualifier("Repo")
    private CsvRepo csvRepo;


    /**
     * Function call another function from repository for find any entry from db
     * @see repository.CsvRepo
     * @return a list of weights from db
     */
    public boolean isFindAll() throws IOException {

        final String path = "Z://JavaProject//csv//temp2.csv";
        List<Weights> weights = csvRepo.findAll();
        if (weights.size() != 0){

            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("word", "value").withDelimiter(','));
            for(Weights weights1: weights)
                csvPrinter.printRecord(weights1.getWord().trim(), weights1.getStr_value());
            csvPrinter.flush();
            return true;
        }
        return false;

    }


    /**
     *
     */
    public void save(MultipartFile multipartFile){

        try{
            XSSFWorkbook wb = new XSSFWorkbook(multipartFile.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();
            while(iterator.hasNext()) {
                Row rowCurrent = iterator.next();
                if(rowCurrent.getCell(1).getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
                    csvRepo.save(rowCurrent.getCell(0).getStringCellValue(),
                        new BigDecimal(rowCurrent.getCell(1).getNumericCellValue()));
            }

        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
