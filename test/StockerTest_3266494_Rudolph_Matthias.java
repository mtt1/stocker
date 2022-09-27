import org.junit.jupiter.api.BeforeEach;

import de.stocker.common.StockerTesterImpl;
import test.StockerTest;

public class StockerTest_3266494_Rudolph_Matthias extends StockerTest {

    @Override
    @BeforeEach
    public void setUp() {
        setStockerTester(new StockerTesterImpl());
    }
}
