package memory;

//import assembler.AsmHandler;
//import system.ProcessScheduler;
//import system.ProcessState;
//import system.ShellCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Process extends Thread {

    public Stack<String> stack = new Stack<>();
    private String processName;
    private String processNameWithID;
    private boolean save = false;
    private String saveFileName;
    private ArrayList<String> instructions = new ArrayList<>();
    private int idProces;
    private int partitionId;
    private String resultString;

    public Process(String filePath, int id) {
        this.processName = filePath;
        this.idProces = id;
        this.processNameWithID = extractProcessNameWithID(filePath, id);
    }

    private String extractProcessNameWithID(String filePath, int id) {
        int x = -1;
        if (filePath.contains("/")) {
            x = filePath.lastIndexOf("/"); //za UNIX sisteme
        } else if (filePath.contains("\\")) {
            x = filePath.lastIndexOf("\\"); //Windows sistemi
        }

        try {
            return filePath.substring(x + 1) + "(" + id + ")";
        } catch (StringIndexOutOfBoundsException e) {
            return filePath + "(" + id + ")";
        }
    }

    public int getIdProces() {
        return idProces;
    }

    public String getSaveFileName() {
        return saveFileName;
    }

    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }


    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public ArrayList<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(ArrayList<String> instructions) {
        this.instructions = instructions;
    }

    public String getProcessName() {
        return processNameWithID;
    }

    public String getFilePath() {
        return this.processName;
    }

    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    public int getPartitionId() {
        return partitionId;
    }
    
    public String getResultString() {
        return resultString;
    }


    @Override
    public void run() {
     /*   try {
            AsmHandler asmHandler = new AsmHandler();
            ScanController.loadProcessIntoRam(this);

            // Proveri da li ima slobodnih particija u RAM-u pre nego što proces počne
            if (!hasEnoughRamSpace()) {
                System.out.println("Nema dovoljno slobodnih particija u RAM-u za proces " + this.getProcessName());
                this.blockProcess(); // Postavi proces u stanje BLOKIRAN
                return; // Prekini dalje izvršavanje metode run ako nema dovoljno RAM-a
            }

            asmHandler.instructionReader(this);

            // Oslobađanje RAM-a nakon završetka procesa
            freeRamResources();
            //freeRamResources(this); //mozda treba dodati ili popraviti jos nesto u funkciji?

            if (this.state == ProcessState.DONE) {
                ShellCommands.threadSet.remove(this); // Ukloni proces iz seta aktivnih niti
                ProcessScheduler.red.remove(this);    // Ukloni proces iz reda za raspoređivanje
            }

            // Ako je potrebno, sačuvaj rezultat na disk
            if (this.save) {
                ScanController.saveProcessToDisk(this);
            }
        } catch (Exception e) {
            System.err.println("Greška u izvršavanju procesa: " + e.getMessage());
        }*/
    }
    private boolean hasEnoughRamSpace(){

        List<FixedPartition>partitions=Ram.getInstance().getPartitions();

        for(FixedPartition partition:partitions){
            if(!partition.isOccupied())
                return true; //postoji bar jedn particija koja je slobodna
        }
        return false;
    }
     public static void freeRamResources(Process process) {
        Ram ram = Ram.getInstance();
        int partitionId = process.getPartitionId();

        for (FixedPartition partition : ram.getPartitions()) {
            if (partition.getPartitionId() == partitionId) {
                partition.free();
                System.out.println("Freed partition " + partitionId + " from process '" + process.getProcessName() + "'");
                break;
            }
        }
    }

    @Override
    public String toString() {
        return processNameWithID;
    }
}
