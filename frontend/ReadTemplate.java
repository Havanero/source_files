package com.eurexchange.clear.frontend;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ReadTemplate {

    private String fileName;

    public ReadTemplate(String pFileName) {
        this.fileName = pFileName;

    }

    public ReadTemplate() {

    }

    public String getTemplate() throws IOException {
        String resources = "/templates/"+fileName;
        InputStream is = getClass().getResourceAsStream(resources);
        System.out.println(resources);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
        byte[] contents = new byte[1024];
        int bytesRead;
        String line;
        StringBuilder sb = new StringBuilder();
        while ((bytesRead = bufferedInputStream.read(contents)) != -1) {
            line = new String(contents, 0, bytesRead);
            sb.append(line);

        }
        return sb.toString();

    }

    public List<String> getAllFiles(String path) {
        List<String> allFiles = new ArrayList<>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                allFiles.add(listOfFile.getName());
            }
        }
        return allFiles;
    }

    public String getPath(String folderName) {
        URL resource = ReadTemplate.class.getResource(folderName);
        Path paths=null;
        try {
             paths = Paths.get(resource.toURI().getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        assert paths != null;
        System.out.println("new Path" + folderName);
        return paths.toFile().toString();

    }

    //    public String getFormattedMessage(String message, String secNum) {
    //
    //        return upDateTradeMessage(message, secNum);
    //    }
    //
    //    private String upDateTradeMessage(String message, String secNum) {
    //        TrdMtchRptType trdMtchRptType = new TrdMtchRptType();
    //        TradeRequest tradeRequest = new TradeRequest(trdMtchRptType);
    //        tradeRequest.setFixmlMessage(message);
    //        return tradeRequest.upDateIntialSecNum(secNum);
    //
    //    }
}