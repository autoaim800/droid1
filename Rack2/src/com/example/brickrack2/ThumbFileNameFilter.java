package com.example.brickrack2;

import java.io.File;
import java.io.FilenameFilter;

public class ThumbFileNameFilter implements FilenameFilter {

    @Override
    public boolean accept(File file, String fileName) {
        return (fileName.endsWith(Rack.EXT_THUMB) && file.isFile());
    }

}
