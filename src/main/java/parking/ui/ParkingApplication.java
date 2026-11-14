package parking.ui;

import parking.core.*;
import parking.model.*;
import parking.util.RecordStore;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Paths;
import java.util.List;

public class ParkingApplication {
    private static final Color NAVY=new Color(17,35,65), BLUE=new Color(35,91,180), BG=new Color(242,245,249), WHITE=Color.WHITE;
    private final ParkingManager manager=new ParkingManager(30,new RecordStore(Paths.get("data","parking-records.csv")));
    private final JLabel total=new JLabel(), available=new JLabel(), occupied=new JLabel(), vehicles=new JLabel(), revenue=new JLabel();
    private final JPanel slotsPanel=new JPanel(new GridLayout(0,5,10,10));
    private final DefaultTableModel historyModel=new DefaultTableModel(new Object[]{"Vehicle","Owner","Type","Slot","Entry","Exit","Fee (₹)"},0) { public boolean isCellEditable(int r,int c){return false;} };
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new ParkingApplication().show()); }
    private void show() {
        JFrame frame=new JFrame("Parkwise | Smart Parking Management"); frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); frame.setSize(1120,780); frame.setMinimumSize(new Dimension(900,650)); frame.setLocationRelativeTo(null);
        JPanel root=new JPanel(new BorderLayout()); root.setBackground(BG); frame.setContentPane(root);
        JPanel sidebar=new JPanel(); sidebar.setPreferredSize(new Dimension(205,0)); sidebar.setBackground(NAVY); sidebar.setLayout(new BoxLayout(sidebar,BoxLayout.Y_AXIS)); sidebar.setBorder(new EmptyBorder(24,18,20,18));
        JLabel brand=new JLabel("PARKWISE"); brand.setForeground(WHITE); brand.setFont(new Font("SansSerif",Font.BOLD,23)); sidebar.add(brand); JLabel sub=new JLabel("SMART PARKING SYSTEM");sub.setForeground(new Color(170,190,220));sub.setFont(new Font("SansSerif",Font.PLAIN,10));sidebar.add(sub);sidebar.add(Box.createVerticalStrut(38));
        root.add(sidebar,BorderLayout.WEST);
        JPanel content=new JPanel(new BorderLayout(0,16));content.setOpaque(false);content.setBorder(new EmptyBorder(25,28,25,28));root.add(content,BorderLayout.CENTER);
        JPanel top=new JPanel(new BorderLayout());top.setOpaque(false);JLabel title=new JLabel("Parking overview");title.setFont(new Font("SansSerif",Font.BOLD,26));title.setForeground(NAVY);top.add(title,BorderLayout.WEST);JLabel date=new JLabel("SMART FACILITY  /  LIVE STATUS");date.setForeground(new Color(105,119,140));top.add(date,BorderLayout.EAST);content.add(top,BorderLayout.NORTH);
        JPanel body=new JPanel();body.setOpaque(false);body.setLayout(new BoxLayout(body,BoxLayout.Y_AXIS));JScrollPane bodyScroll=new JScrollPane(body);bodyScroll.setBorder(BorderFactory.createEmptyBorder());bodyScroll.getVerticalScrollBar().setUnitIncrement(18);content.add(bodyScroll,BorderLayout.CENTER);
        JPanel cards=new JPanel(new GridLayout(0,3,12,12));cards.setOpaque(false);cards.add(card("TOTAL SLOTS",total,"▦"));cards.add(card("AVAILABLE",available,"✓"));cards.add(card("OCCUPIED",occupied,"●"));cards.add(card("PARKING VISITS",vehicles,"↗"));cards.add(card("REVENUE",revenue,"₹"));body.add(cards);body.add(Box.createVerticalStrut(12));
        JPanel capacityPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,10,8));capacityPanel.setBackground(WHITE);capacityPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(225,231,239)),new EmptyBorder(6,10,6,10)));JLabel capacityLabel=new JLabel("Parking capacity: "+manager.getTotalSlots()+" slots");capacityLabel.setFont(new Font("SansSerif",Font.BOLD,14));capacityLabel.setForeground(NAVY);JButton decrease=button("− Remove slot"),increase=button("+ Add slot");capacityPanel.add(capacityLabel);capacityPanel.add(decrease);capacityPanel.add(increase);decrease.addActionListener(e->changeCapacity(-1,capacityLabel));increase.addActionListener(e->changeCapacity(1,capacityLabel));body.add(capacityPanel);body.add(Box.createVerticalStrut(18));
        JPanel forms=new JPanel(new GridLayout(0,1,0,14));forms.setOpaque(false);forms.add(registrationPanel());forms.add(exitPanel());body.add(forms);body.add(Box.createVerticalStrut(18));
        JPanel slotBox=box("Live parking availability");JLabel legend=new JLabel("  ● Available     ● Occupied");legend.setForeground(new Color(95,110,130));slotBox.add(legend,BorderLayout.SOUTH);slotsPanel.setOpaque(false);slotBox.add(slotsPanel,BorderLayout.CENTER);body.add(slotBox);body.add(Box.createVerticalStrut(16));
        JPanel history=box("Parking history");JTable table=new JTable(historyModel);table.setRowHeight(28);table.setAutoCreateRowSorter(true);history.add(new JScrollPane(table),BorderLayout.CENTER);body.add(history);
        addNavigation(sidebar, bodyScroll, cards, slotBox, history);
        refresh(); frame.setVisible(true);
    }
    private void addNavigation(JPanel sidebar, JScrollPane scroll, JComponent overview, JComponent slots, JComponent history) {
        sidebar.add(Box.createVerticalStrut(38));
        addNavButton(sidebar, "◈  Overview", scroll, overview);
        addNavButton(sidebar, "▦  Parking slots", scroll, slots);
        addNavButton(sidebar, "▤  Vehicle history", scroll, history);
    }
    private void addNavButton(JPanel sidebar, String label, JScrollPane scroll, JComponent section) {
        JButton nav = new JButton(label);
        nav.setHorizontalAlignment(SwingConstants.LEFT);
        nav.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nav.setForeground(WHITE); nav.setBackground(NAVY); nav.setBorder(new EmptyBorder(13,10,13,4));
        nav.setFocusPainted(false); nav.setContentAreaFilled(false); nav.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nav.addActionListener(e -> {
            Point point = SwingUtilities.convertPoint(section, 0, 0, scroll.getViewport());
            scroll.getViewport().setViewPosition(new Point(0, Math.max(0, point.y - 8)));
        });
        sidebar.add(nav);
    }
    private JPanel card(String label,JLabel value,String icon){JPanel p=new JPanel(new BorderLayout(8,8));p.setBackground(WHITE);p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(225,231,239)),new EmptyBorder(14,14,14,14)));JLabel l=new JLabel(label);l.setFont(new Font("SansSerif",Font.BOLD,10));l.setForeground(new Color(103,117,136));value.setFont(new Font("SansSerif",Font.BOLD,24));value.setForeground(NAVY);JLabel i=new JLabel(icon);i.setForeground(BLUE);i.setFont(new Font("SansSerif",Font.BOLD,20));p.add(l,BorderLayout.NORTH);p.add(value,BorderLayout.CENTER);p.add(i,BorderLayout.EAST);return p;}
    private JPanel box(String title){JPanel p=new JPanel(new BorderLayout(8,10));p.setBackground(WHITE);p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(225,231,239)),new EmptyBorder(14,15,14,15)));JLabel l=new JLabel(title);l.setFont(new Font("SansSerif",Font.BOLD,15));l.setForeground(NAVY);p.add(l,BorderLayout.NORTH);return p;}
    private JPanel registrationPanel(){JPanel p=box("Register and park vehicle");JPanel f=new JPanel(new GridLayout(0,2,8,8));f.setOpaque(false);JTextField number=new JTextField(),owner=new JTextField();number.setToolTipText("Use this same plate number to search or exit later");JComboBox<String> type=new JComboBox<>(new String[]{"Car","Bike","Truck"});f.add(new JLabel("Registration / plate number"));f.add(number);f.add(new JLabel("Owner name"));f.add(owner);f.add(new JLabel("Vehicle type"));f.add(type);JButton park=button("Register & park");f.add(new JLabel("Rates: Bike ₹20 · Car ₹40 · Truck ₹60 per hour"));f.add(park);park.addActionListener(e->{try{Vehicle v=make((String)type.getSelectedItem(),number.getText(),owner.getText());ParkingTicket t=manager.park(v);refresh();JOptionPane.showMessageDialog(p,"Vehicle parked in slot "+t.getSlotNumber()+".\nUse registration "+v.getRegistrationNumber()+" to search or exit.");number.setText("");owner.setText("");}catch(Exception ex){JOptionPane.showMessageDialog(p,ex.getMessage(),"Booking failed",JOptionPane.WARNING_MESSAGE);}});p.add(f,BorderLayout.CENTER);return p;}
    private JPanel exitPanel(){JPanel p=box("Search or exit vehicle");JPanel f=new JPanel(new GridLayout(0,2,8,8));f.setOpaque(false);JTextField number=new JTextField();number.setToolTipText("Enter the exact plate number used when parking");f.add(new JLabel("Enter the same registration / plate number"));f.add(number);JButton exit=button("Complete exit");JButton search=button("Search active stay");f.add(search);f.add(exit);search.addActionListener(e->{ParkingTicket t=manager.findActive(number.getText());JOptionPane.showMessageDialog(p,t==null?"No active stay found. Check the registration number used when parking.":t.getVehicle()+"\nSlot "+t.getSlotNumber()+" · Entry "+t.getEntryTime());});exit.addActionListener(e->{try{ParkingTicket t=manager.exit(number.getText());refresh();JOptionPane.showMessageDialog(p,"Exit recorded. Parking fee: ₹"+String.format("%.2f",t.getFee()));number.setText("");}catch(Exception ex){JOptionPane.showMessageDialog(p,ex.getMessage(),"Exit failed",JOptionPane.WARNING_MESSAGE);}});p.add(f,BorderLayout.CENTER);return p;}
    private JButton button(String label){JButton b=new JButton(label);b.setBackground(BLUE);b.setForeground(WHITE);b.setFocusPainted(false);return b;}
    private void changeCapacity(int delta,JLabel label){try{manager.setCapacity(manager.getTotalSlots()+delta);label.setText("Parking capacity: "+manager.getTotalSlots()+" slots");refresh();}catch(ParkingException ex){JOptionPane.showMessageDialog(null,ex.getMessage(),"Capacity not changed",JOptionPane.WARNING_MESSAGE);}}
    private Vehicle make(String type,String number,String owner){if("Bike".equals(type))return new Bike(number,owner);if("Truck".equals(type))return new Truck(number,owner);return new Car(number,owner);}
    private void refresh(){total.setText(""+manager.getTotalSlots());available.setText(""+manager.getAvailableCount());occupied.setText(""+manager.getOccupiedCount());vehicles.setText(""+manager.getVehicleCount());revenue.setText("₹"+String.format("%.0f",manager.getRevenue()));int columns=Math.max(3,Math.min(7,manager.getTotalSlots()));slotsPanel.setLayout(new GridLayout(0,columns,10,10));slotsPanel.removeAll();for(ParkingSlot s:manager.getSlots()){JPanel tile=new JPanel(new BorderLayout(2,4));tile.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(s.isAvailable()?new Color(194,231,211):new Color(244,203,196)),new EmptyBorder(10,10,10,10)));tile.setBackground(s.isAvailable()?new Color(235,249,242):new Color(255,242,239));JLabel name=new JLabel("SLOT "+String.format("%02d",s.getNumber()));name.setFont(new Font("SansSerif",Font.BOLD,12));name.setForeground(NAVY);tile.add(name,BorderLayout.NORTH);JLabel status=new JLabel(s.isAvailable()?"AVAILABLE":s.getVehicle().getRegistrationNumber());status.setFont(new Font("SansSerif",Font.PLAIN,11));status.setForeground(s.isAvailable()?new Color(29,132,83):new Color(185,72,57));tile.add(status,BorderLayout.CENTER);slotsPanel.add(tile);}historyModel.setRowCount(0);List<ParkingTicket> hs=manager.getHistory();for(int i=hs.size()-1;i>=0;i--){ParkingTicket t=hs.get(i);historyModel.addRow(new Object[]{t.getVehicle().getRegistrationNumber(),t.getVehicle().getOwnerName(),t.getVehicle().getType(),t.getSlotNumber(),t.getEntryTime(),t.getExitTime()==null?"—":t.getExitTime(),String.format("%.2f",t.getFee())});}slotsPanel.revalidate();slotsPanel.repaint();}
}
