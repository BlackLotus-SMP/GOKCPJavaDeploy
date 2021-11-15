package main;

import input.InputHandler;
import kcp.KCPConfig;
import kcp.KCPDownloader;
import kcp.KCPExecutor;
import utils.OSUtils;

import java.io.File;

public class Main {
    private static String DATA_DIR = "data";
    private static String TARGZ_DIR = "compressed";
    private static String CONFIG_DIR = "config";
    private static String CONFIG_FILE = "config.json";

    public static void main(String[] args) {
        System.out.printf("Looking for KCP clients... OS: %s; Arch: %s;%n", OSUtils.getOSName(), OSUtils.getArch());
        new Thread(InputHandler::processConsoleInput).start();

        KCPDownloader kcpDownloader = new KCPDownloader(getFileStart());
        String url = kcpDownloader.getValidTarGZUrl();
        if (url == null) {
            System.err.println("Unable to find a valid KCP client...");
            return;
        }
        System.out.printf("Valid KCP client found! > %s%n", url);
        @SuppressWarnings("unused")
        boolean createMainDir = new File(DATA_DIR + File.separator + TARGZ_DIR).mkdirs();
        @SuppressWarnings("unused")
        boolean createMainDir2 = new File(DATA_DIR + File.separator + CONFIG_DIR).mkdirs();
        boolean downloaded = kcpDownloader.downloadCompressed(DATA_DIR + File.separator + TARGZ_DIR, url);
        if (!downloaded) {
            System.err.println("Error trying to download the compressed binary.");
            return;
        }
        System.out.println("Downloaded kcp.tar.gz file");
        boolean unCompressed = kcpDownloader.unTar(DATA_DIR + File.separator + TARGZ_DIR, DATA_DIR);
        if (!unCompressed) {
            System.err.println("Error on unTar kcp.tar.gz, unable to uncompress.");
            return;
        }
        File clientFile = kcpDownloader.getClientBinFileString(DATA_DIR);
        if (clientFile == null) {
            System.err.println("Unable to get a valid client binary.");
            return;
        }
        if (!clientFile.canExecute()) {
            boolean executable = clientFile.setExecutable(true);
            if (!executable) {
                System.err.println("Unable to set this client binary executable.");
                return;
            }
        }

        System.out.println("Got a valid client!");
        File configFile = new File(DATA_DIR + File.separator + CONFIG_DIR + File.separator + CONFIG_FILE);
        if (!configFile.exists()) {
            System.out.println("Config file not found... Creating a default one...");
            new KCPConfig().createConfigFile(DATA_DIR + File.separator + CONFIG_DIR + File.separator + CONFIG_FILE);
        }
        System.out.println("Config file loaded!");
        System.out.println("Starting KCP client...");
        new KCPExecutor().runBinWithConfig(
                DATA_DIR,
                clientFile.getName(),
                CONFIG_DIR + File.separator + CONFIG_FILE,
                OSUtils.getOSName()
        );
    }

    private static String getFileStart() {
        return String.format("kcptun-%s-%s-", OSUtils.getOSName(), OSUtils.getArch());
    }
}
