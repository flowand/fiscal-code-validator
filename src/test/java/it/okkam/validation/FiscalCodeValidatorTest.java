package it.okkam.validation;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class FiscalCodeValidatorTest {

  private static final String CODICE_ISTAT_COMUNI_CSV = "codice-istat-comuni.csv";
  private static FiscalCodeConf conf1;
  private static FiscalCodeConf conf2;

  private String readLocalFile(String filePath) throws IOException {
    try {
      ClassLoader classLoader = this.getClass().getClassLoader();
      return IOUtils.toString(classLoader.getResource(filePath), Charset.forName("UTF-8"));
    } catch (IOException ex) {
      throw ex;
    }
  }

  private static boolean arrayContains(String[] array, final String expected) {
    boolean found = false;
    for (String val : array) {
      if (expected.equals(val)) {
        found = true;
        break;
      }
    }
    return found;
  }

  @Test
  public void testComuniMapInitialization() throws IOException {
    String codiciIstatStr = readLocalFile(CODICE_ISTAT_COMUNI_CSV);
    Map<String, List<String>> comuniMap = FiscalCodeValidator.getComuniMap(codiciIstatStr);
    Assert.assertEquals("F205", comuniMap.get("MILANO").get(0));
  }

  /**
   * Init method.
   * 
   * @throws IOException
   */
  @Before
  public void init() throws IOException {
    String codiciIstatStr = readLocalFile(CODICE_ISTAT_COMUNI_CSV);
    conf1 = FiscalCodeValidator.getFiscalCodeConf(codiciIstatStr, "M", 8, 10, 3, 5, 0, 2);
    conf2 = FiscalCodeValidator.getFiscalCodeConf(codiciIstatStr, "M", 2, 4, 5, 7, 8, 10);
  }

  @Test
  public void testEmptyValues() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(conf1, "XX", null, null, null, "M");
    Assert.assertNull(codes);
  }

  @Test
  public void testDarioFo() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(conf1, "FO", "DARIO", "24/03/1926",
        "SANGIANO", "M");
    Assert.assertTrue(arrayContains(codes, "FOXDRA26C24H872Y"));
  }

  @Test
  public void testDateTimePattern() {
    String[] codes = FiscalCodeValidator.calcoloCodiceFiscale(conf2, "FO", "DARIO",
        "1926-03-24T00:00:00", "SANGIANO", "M");
    Assert.assertTrue(arrayContains(codes, "FOXDRA26C24H872Y"));
  }
}
