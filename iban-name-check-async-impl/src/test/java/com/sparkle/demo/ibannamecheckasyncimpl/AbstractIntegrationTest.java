package com.sparkle.demo.ibannamecheckasyncimpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

public abstract class AbstractIntegrationTest {

    @Value("classpath:unit-test/excel/file.xlsx")
    protected Resource excelFile;
}
