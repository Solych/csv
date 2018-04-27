package service;

import model.Lines;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Pavel on 27.04.2018.
 */
public interface WriteService {
    Lines write(MultipartFile file) throws Exception;
}
