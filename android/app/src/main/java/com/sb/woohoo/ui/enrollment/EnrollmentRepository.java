package com.sb.woohoo.ui.enrollment;

import android.content.Context;
import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.enrollment.sensory.EnrollHelper;

import java.io.File;

import javax.inject.Inject;

public class EnrollmentRepository
        extends BaseRepository<EnrollmentPresenter> {
    // Constants.
    private final String ENROLL_OUTPUT_FILE = "enrolled.snsr";

    @Inject
    EnrollmentRepository() {
    }

    public boolean doesEnrollmentFileExists(Context context) {
        File enrollOutDir = EnrollHelper.getOutDir(context);
        File enrollOutFile = new File(enrollOutDir, ENROLL_OUTPUT_FILE);
        LoggerWrapper.d("enrollOutFile", enrollOutFile.getAbsolutePath());
        return enrollOutFile.exists();
    }
}