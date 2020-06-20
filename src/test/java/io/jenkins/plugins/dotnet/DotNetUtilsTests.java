package io.jenkins.plugins.dotnet;

import org.junit.Assert;
import org.junit.Test;

public class DotNetUtilsTests {

  @Test
  public void normalizeList_delimitersBecomeSingleSpace() {
    Assert.assertEquals("a b c", DotNetUtils.normalizeList("a b c"));
    Assert.assertEquals("a b c", DotNetUtils.normalizeList("a   b     c"));
    Assert.assertEquals("a b c", DotNetUtils.normalizeList("    a b                   c                     "));
    Assert.assertEquals("a b c", DotNetUtils.normalizeList("a,b,c"));
    Assert.assertEquals("a b c", DotNetUtils.normalizeList("a,,,,,b,c"));
    Assert.assertEquals("a b c", DotNetUtils.normalizeList("a;b;c"));
    Assert.assertEquals("a b c", DotNetUtils.normalizeList("a;b;;;;;c"));
    Assert.assertEquals("a b c", DotNetUtils.normalizeList("    a,b     ,      c;         ,"));
  }

  @Test
  public void normalizeList_emptyListBecomesNull() {
    Assert.assertNull(DotNetUtils.normalizeList(""));
    Assert.assertNull(DotNetUtils.normalizeList("        "));
    Assert.assertNull(DotNetUtils.normalizeList(","));
    Assert.assertNull(DotNetUtils.normalizeList(",,,,,,"));
    Assert.assertNull(DotNetUtils.normalizeList(";"));
    Assert.assertNull(DotNetUtils.normalizeList(";;;;"));
    Assert.assertNull(DotNetUtils.normalizeList(" , ; , "));
  }

}
