import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.hamcrest.MatcherAssert.assertThat;

public class homeWork {
    ClassLoader cl = homeWork.class.getClassLoader();

    @DisplayName("разбор json файла библиотекой Jackson")
    @Test
    void jsonTest() throws Exception {
        File file = new File("src/test/resources/homeWork/dendi.json");
        ObjectMapper objectMapper = new ObjectMapper();
        dendiModel dendiModel = objectMapper.readValue(file, dendiModel.class);
        assertThat(dendiModel.name).isEqualTo("Данил");
        assertThat(dendiModel.hero.mid).isEqualTo("Pudge");
        assertThat(dendiModel.org.get(0)).isEqualTo("NAVI");
    }

    @DisplayName("Parsing zip/pdf file")
    @Test
    void zipTest() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/homeWork/homeWork.zip"));
        try (ZipInputStream zip = new ZipInputStream(cl.getResourceAsStream("homeWork/homeWork.zip"));) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().contains(".pdf")) {
                    try (InputStream inputStream = zf.getInputStream(entry)) {
                        PDF pdf = new PDF(inputStream);
                        assertThat(pdf.text).contains("JUnit 5 User Guide");
                        assertThat(pdf.author).contains("Sam Brannen");
                    }
                }
            }
        }
    }

    @DisplayName("Parsing zip/xls file")
    @Test
    void zipTest1() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/homeWork/homeWork.zip"));
        try (ZipInputStream zip = new ZipInputStream(cl.getResourceAsStream("homeWork/homeWork.zip"));) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().contains(".xls")) {
                    try (InputStream inputStream = zf.getInputStream(entry)) {
                        XLS xls = new XLS(inputStream);
                        assertThat(xls.excel.getSheetAt(0)
                                .getRow(4).getCell(2)
                                .getStringCellValue())
                                .isEqualTo("Rus 5");
                    }
                }
            }
        }
    }

    @DisplayName("Parsing zip/csv file")
    @Test
    void zipTest2() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/homeWork/homeWork.zip"));
        try (ZipInputStream zip = new ZipInputStream(cl.getResourceAsStream("homeWork/homeWork.zip"));) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().contains(".csv")) {
                    try (InputStream inputStream = zf.getInputStream(entry)) {
                        CSVReader csv = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                        List<String[]> content = csv.readAll();
                        String[] row = content.get(6);
                        assertThat(row[2]).isEqualTo("Rus 7");
                    }
                }
            }
        }
    }
}