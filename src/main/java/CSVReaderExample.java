import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import dat.Intel;
import dat.IntelDAO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CSVReaderExample {
    public static void main(String[] args) throws Exception {
        List<Intel> intelList = new ArrayList<>();

        File file = new File("fullintels.csv");
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        MappingIterator<Intel> iterator = mapper.readerFor(Intel.class).with(schema).readValues(file);

        while (iterator.hasNext()) {
            Intel intel = iterator.next();
            intelList.add(intel);
        }

        IntelDAO intelDAO = new IntelDAO();

        intelDAO.createTable();
        // Insert the list of Intel objects into the database
        intelDAO.insertIntels(intelList);

        System.out.println("Intels inserted successfully!");
    }
}
