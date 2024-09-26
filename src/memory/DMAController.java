package memory;

//import system.ShellCommands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DMAController {

    public static void fromDiskToRam(Process process) {
        Ram ram = Ram.getInstance();
        ArrayList<FixedPartition> partitions = ram.getPartitions();
        ArrayList<String> instructions = process.getInstructions();
        int instructionIndex = 0;

        try {
            for (FixedPartition partition : partitions) {
                if (!partition.isOccupied()) {
                    ArrayList<String> partitionData = new ArrayList<>();

                    while (instructionIndex < instructions.size() && partitionData.size() < FixedPartition.SIZE) {
                        partitionData.add(instructions.get(instructionIndex));
                        instructionIndex++;
                    }

                    partition.allocate(process.getProcessName(), partitionData);

                    if (instructionIndex >= instructions.size()) {
                        break;
                    }
                }
            }

            if (instructionIndex < instructions.size()) {
                System.out.println("Nema dovoljno slobodnih particija u RAM-u za ceo proces.");
                //process.blockProcess(); // blok proces ako nema dovoljno RAM-a
            }

        } catch (Exception e) {
            System.err.println("Greška prilikom učitavanja procesa iz diska u RAM: " + e.getMessage());
        }
    }

    // Prebacivanje podataka iz RAM-a na disk
  /*  public static void fromRamToDisk(Process process) {
        Ram ram = Ram.getInstance();
        ArrayList<FixedPartition> partitions = ram.getPartitions();

        try {
            // Kreiramo fajl na disku da sačuvamo sadržaj procesa
            File newFile = new File(ShellCommands.getCurrentDir() + "\\" + process.getSaveFileName() + ".txt");
            if (!newFile.exists()) {
                newFile.createNewFile();
                FileWriter writer = new FileWriter(newFile);

                // Prelazimo kroz RAM particije koje pripadaju ovom procesu
                for (FixedPartition partition : partitions) {
                    if (partition.isOccupied() && partition.getProcessName().equals(process.getProcessName())) {
                        // Upisujemo sadržaj particije na disk
                        for (String line : partition.getContent()) {
                            writer.write(line + "\n");
                        }
                        partition.free(); // Oslobađamo particiju nakon prebacivanja
                    }
                }

                writer.close();

                // Simuliramo dodavanje fajla u memoriju diska
                FileInMemory fileInMemory = new FileInMemory(process.getSaveFileName(), (int) newFile.length(), null);
                Disc.listOfFiles.add(fileInMemory);
            }
        } catch (IOException e) {
            System.err.println("Greška prilikom prebacivanja procesa iz RAM-a na disk: " + e.getMessage());
        }
    }*/
}
