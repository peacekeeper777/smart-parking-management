package parking.util;
import parking.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

/** Simple CSV storage. The parser supports quoted commas and escaped quotes. */
public class RecordStore {
    private final Path file;
    public RecordStore(Path file) { this.file=file; }
    public synchronized void save(List<ParkingTicket> tickets) throws IOException {
        Path parent=file.toAbsolutePath().getParent(); if(parent!=null) Files.createDirectories(parent);
        try(BufferedWriter w=Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            w.write("registration,owner,type,slot,entry,exit,fee"); w.newLine();
            for(ParkingTicket t:tickets) { w.write(t.toCsv()); w.newLine(); }
        }
    }
    public synchronized List<ParkingTicket> load() {
        List<ParkingTicket> result=new ArrayList<ParkingTicket>();
        if(!Files.exists(file)) return result;
        try(BufferedReader r=Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line=r.readLine();
            while((line=r.readLine())!=null) try {
                List<String> c=parse(line); Vehicle v=create(c.get(2),c.get(0),c.get(1));
                LocalDateTime exit=c.get(5).isEmpty()?null:LocalDateTime.parse(c.get(5));
                ParkingTicket t=ParkingTicket.restore(v,Integer.parseInt(c.get(3)),LocalDateTime.parse(c.get(4)),exit,Double.parseDouble(c.get(6)));
                result.add(t);
            } catch(Exception badRow) { System.err.println("Skipping invalid parking record: "+badRow.getMessage()); }
        } catch(IOException e) { System.err.println("Could not read records: "+e.getMessage()); }
        return result;
    }
    private static Vehicle create(String type,String number,String owner) {
        if("Car".equals(type)) return new Car(number,owner); if("Bike".equals(type)) return new Bike(number,owner); return new Truck(number,owner);
    }
    private static List<String> parse(String line) {
        List<String> out=new ArrayList<String>(); StringBuilder b=new StringBuilder(); boolean quoted=false;
        for(int i=0;i<line.length();i++){ char ch=line.charAt(i); if(ch=='"'){ if(quoted&&i+1<line.length()&&line.charAt(i+1)=='"'){b.append('"');i++;}else quoted=!quoted;} else if(ch==','&&!quoted){out.add(b.toString());b.setLength(0);}else b.append(ch); }
        out.add(b.toString()); if(out.size()!=7) throw new IllegalArgumentException("Expected seven CSV columns"); return out;
    }
}
