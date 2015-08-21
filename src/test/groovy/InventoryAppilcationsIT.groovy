import io.dbmaster.testng.BaseToolTestNGCase;

import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test

import com.branegy.tools.api.ExportType;


public class InventoryAppilcationsIT extends BaseToolTestNGCase {

    @Test
    public void test() {
        def parameters = [ : ]
        String result = tools.toolExecutor("inventory-applications", parameters).execute()
        assertTrue(result.contains("Application"), "Unexpected search results ${result}");
    }
}
