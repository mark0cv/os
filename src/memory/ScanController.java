package memory;

import system.ShellCommands;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ScanController {

    private static List<DiskRequest> requestQueue = new LinkedList<>();

    public static void fromDiskToRam(Process process) {
        try {
            process.setInstructions((ArrayList<String>) Files.readAllLines(Paths.get(process.getFilePath())));
        } catch (IOException e) {
            System.err.println("Error reading ASM file: " + e.getMessage());
        }

        process.setNumOfPages(process.getInstructions().size() / Page.SIZE);
        int br = 0;

        for (int i = 0; i < process.getNumOfPages(); i++) {
            Page p = new Page(i, process.getProcessName());
            int x = 0;

            while (x < Page.SIZE) {
                if (br < process.getInstructions().size())
                    p.getContent().add(process.getInstructions().get(br));
                x++;
                br++;
            }

            for (int j = 0; j < Ram.NumOfFrames; j++) {
                if (Ram.frames[j] == 0) {
                    Ram.frames[j] = 1;
                    Ram.memory.put(j, p);
                    process.getPageTable().put(i, j);
                    break;
                }
            }
        }
    }

    public static void fromRamToDisk(Process process) {
        try {

            File newFile = new File(ShellCommands.getCurrentDir() + "\\" + process.getSaveFileName() + ".txt");
            if (!newFile.exists()) {

                for (FileInMemory f : Disc.listaFile) {
                    if (f.getName().equals(ShellCommands.trenutniDirNaziv)) {
                        FileInMemory fim = new FileInMemory(process.getSaveFileName(), 1, f);
                        Disc.listaFile.add(fim);
                        f.getPodfajlovi().add(fim);
                        newFile.createNewFile();

                        try (FileWriter fw = new FileWriter(newFile)) {
                            String message = "Rezultat izvrsavanja: " + process.getRezultat();
                            fw.write(message);
                        }

                        ArrayList<String> contentList = new ArrayList<>();
                        contentList.add("Rezultat izvrsavanja: " + process.getRezultat());
                        fim.setContent(contentList);

                        requestQueue.add(new DiskRequest(fim, newFile));

                        executeScan();

                        break;
                    } else {
                        System.out.println("Directory already exists.");
                        return;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeScan() {
        int currentPosition = 0;
        boolean movingRight = true;

        while (!requestQueue.isEmpty()) {
            if (movingRight) {

                for (int i = 0; i < requestQueue.size(); i++) {
                    DiskRequest request = requestQueue.get(i);
                    if (request.getFileName().compareTo(String.valueOf(currentPosition)) >= 0) {
                        System.out.println("Processing request for file: " + request.getFile().getName());
                        requestQueue.remove(i);
                        i--;
                        currentPosition++;
                    }
                }

                if (currentPosition >= Ram.NumOfFrames) {
                    movingRight = false;
                    currentPosition = Ram.NumOfFrames - 1;
                }
            } else {

                for (int i = requestQueue.size() - 1; i >= 0; i--) {
                    DiskRequest request = requestQueue.get(i);
                    if (request.getFileName().compareTo(String.valueOf(currentPosition)) <= 0) {
                        System.out.println("Processing request for file: " + request.getFile().getName());
                        requestQueue.remove(i);
                        currentPosition--;
                    }
                }
                if (currentPosition <= 0) {
                    movingRight = true;
                    currentPosition = 0;
                }
            }
        }
    }
}

