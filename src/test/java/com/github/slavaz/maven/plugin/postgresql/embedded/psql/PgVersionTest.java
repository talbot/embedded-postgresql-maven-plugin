package com.github.slavaz.maven.plugin.postgresql.embedded.psql;

import de.flapdoodle.embed.process.distribution.IVersion;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

import static org.testng.AssertJUnit.assertEquals;


/**
 * Created by slavaz on 07/07/17.
 */
public class PgVersionTest {

    @DataProvider(name = "get_DataProvider")
    protected Object[][] getVersionsDataSource() {
        // @formatter:off
        return new Object[][] {
                { null, Version.V11_1 },
                {"latest", Version.V11_1 },
                {"default", Version.V11_1 },
                {"9.4", VersionEx.V9_4_14},
                {"9.5", Version.V9_5_15},
                {"9.5.15", Version.V9_5_15},
                {"9.6", Version.V9_6_11},
                {"9.6.11", Version.V9_6_11},
                {"bla-bla-bla", Version.V11_1 },

        };
        // @formatter:on
    }

    @Test(dataProvider = "get_DataProvider")
    public void getTest(final String inputAlias, final IVersion expectedResult) {

        final IVersion actualResult = PgVersion.get(inputAlias);

        assertEquals(expectedResult, actualResult);
    }
}
