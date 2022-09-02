package org.mammothplugins.groupbonds.settings;

import org.mineacademy.fo.settings.SimpleSettings;

import java.util.Arrays;
import java.util.List;

public final class Settings extends SimpleSettings {

    @Override
    protected int getConfigVersion() {
        return 1;
    }

    /**
     * Place the sections where user can create new "key: value" pairs
     * here so that they are not removed while adding comments.
     * <p>
     * Example use in ChatControl: user can add new channels in "Channels.List"
     * section so we place "Channels.List" here.
     *
     * @return the ignored sections
     */
    @Override
    protected List<String> getUncommentedSections() {
        return Arrays.asList(
                "Example.Uncommented_Section");
    }

    public static class SampleSection {

        public static Boolean SAMPLE_FLAG;

        /*
         * Automatically called method when we load settings.yml to load values in this subclass
         */
        private static void init() {

            // A convenience method to instruct the loader to prepend all paths with Example so you
            // do not have to call "Example.Key1", "Example.Key2" all the time, only "Key1" and "Key2".
            setPathPrefix("Example");

            SAMPLE_FLAG = getBoolean("Sample_Flag");
        }
    }

    /*
     * Automatically called method when we load settings.yml to load values in this class
     *
     * See above for usage.
     */
    private static void init() {
        setPathPrefix(null);
    }
}
