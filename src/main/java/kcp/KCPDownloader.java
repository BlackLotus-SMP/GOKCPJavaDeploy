package kcp;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class KCPDownloader {
    private final String fileStart;

    public KCPDownloader(String fileStart) {
        this.fileStart = fileStart;
    }

    public String getValidTarGZUrl() {
        try {
            StringBuilder res = new StringBuilder();
            String RELEASE = "https://api.github.com/repos/xtaci/kcptun/releases/latest";
            HttpURLConnection conn = (HttpURLConnection) new URL(RELEASE).openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            for (String line; (line = reader.readLine()) != null;) {
                res.append(line);
            }
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(res.toString());
            JSONArray assetsArray = (JSONArray) json.get("assets");
            if (assetsArray == null) {
                System.err.println("Failed to get latest assets...");
                return null;
            }
            for (Object asset : assetsArray) {
                if (((String) ((JSONObject) asset).get("name")).startsWith(this.fileStart)) {
                    return (String) ((JSONObject) asset).get("browser_download_url");
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean downloadCompressed(String path, String url) {
        emptyDir(path);

        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream()); FileOutputStream fileOutputStream = new FileOutputStream(path + "/kcp.tar.gz")) {
            byte[] data = new byte[2048];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOutputStream.write(data, 0, byteContent);
            }
            inputStream.close();
            fileOutputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void emptyDir(String path) {
        File compressedDir = new File(path);
        if (compressedDir.isDirectory()) {
            File[] files = compressedDir.listFiles();
            assert files != null;
            for (File f : files) {
                if (f.isDirectory()) {
                    continue;
                }
                @SuppressWarnings("unused")
                boolean deleted = f.delete();
            }
        }

    }

    public boolean unTar(String compressedDirPath, String resultDir) {
        emptyDir(resultDir);
        TarArchiveInputStream tarArchiveInputStream = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(compressedDirPath + "/kcp.tar.gz");
            GZIPInputStream gzipInputStream = new GZIPInputStream(new BufferedInputStream(fileInputStream));
            tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream);
            TarArchiveEntry tarArchiveEntry;
            while ((tarArchiveEntry = tarArchiveInputStream.getNextTarEntry()) != null) {
                if (!tarArchiveEntry.isDirectory()) {
                    File out = new File(resultDir + File.separator + tarArchiveEntry.getName());
                    FileOutputStream fileOutputStream = new FileOutputStream(out);
                    @SuppressWarnings("unused")
                    boolean created = out.getParentFile().mkdirs();
                    IOUtils.copy(tarArchiveInputStream, fileOutputStream);
                    fileOutputStream.close();
                }
            }
            gzipInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (tarArchiveInputStream != null) {
                try {
                    tarArchiveInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public File getClientBinFileString(String binPath) {
        File binDir = new File(binPath);
        if (binDir.isDirectory()) {
            File[] files = binDir.listFiles();
            assert files != null;
            for (File f : files) {
                if (f.isFile() && f.getName().startsWith("client")) {
                    return f;
                }
            }
        }
        return null;
    }
}
