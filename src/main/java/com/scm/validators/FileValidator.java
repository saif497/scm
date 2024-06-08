package com.scm.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile>{

    //max file size
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 2; // 2MB

    //type of file
    //height
    //width


    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File connot be empty").addConstraintViolation();
            return false;
        }

        if (file.getSize()> MAX_FILE_SIZE){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File size should be less than 2MB").addConstraintViolation();
            return false;

        }


//        try {
//            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

            return true;
    }
}
