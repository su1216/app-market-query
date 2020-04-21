package com.su.market.query.shell;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShellUtil {

    public static final String TAG = ShellUtil.class.getSimpleName();

    public static List<String> getAllApps() {
        String packagesString = shellExecIgnoreExitCode("pm list packages | cut -d ':' -f 2");
        if (TextUtils.isEmpty(packagesString)) {
            return new ArrayList<>();
        }
        return Arrays.asList(packagesString.split("\n"));
    }

    private static String shellExecIgnoreExitCode(@NonNull String... cmd) {
        CommandResult result = shellExec(cmd);
        if (result == null) {
            return "";
        }
        if (result.getLines().isEmpty()) {
            return "";
        }
        return result.getLinesString();
    }

    private static CommandResult shellExec(@NonNull String... cmd) {
        List<String> stdout = new ArrayList<>();
        List<String> stderr = new ArrayList<>();
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec("sh");
            DataOutputStream stdin = new DataOutputStream(process.getOutputStream());
            StreamGobbler stdoutGobbler = new StreamGobbler(process.getInputStream(), stdout);
            StreamGobbler stderrGobbler = new StreamGobbler(process.getErrorStream(), stderr);
            stdoutGobbler.start();
            stderrGobbler.start();

            for (String write : cmd) {
                stdin.write((write + "\n").getBytes(StandardCharsets.UTF_8));
                stdin.flush();
            }
            stdin.write("exit\n".getBytes(StandardCharsets.UTF_8));
            stdin.flush();

            stdoutGobbler.join();
            stderrGobbler.join();
            int exitCode = process.waitFor();
            return new CommandResult(exitCode, stdout);
        } catch (IOException e) {
            Log.e(TAG, "cmd: " + Arrays.toString(cmd), e);
        } catch (InterruptedException e) {
            Log.e(TAG, "cmd: " + Arrays.toString(cmd), e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return null;
    }
}
