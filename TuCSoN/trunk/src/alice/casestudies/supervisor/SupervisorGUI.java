package alice.casestudies.supervisor;

import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

/**
 * 
 * Supervisor GUI ( LIGHT SUPERVISOR CASE STUDY )
 * 
 * @author Steven Maraldi
 */
public final class SupervisorGUI extends javax.swing.JFrame {

    private static SupervisorGUI lightGui;
    /**
	 * 
	 */
    private static final long serialVersionUID = -5500275061372118361L;

    public static SupervisorGUI getLightGUI() {
        if (SupervisorGUI.lightGui == null) {
            SupervisorGUI.lightGui = new SupervisorGUI();
        }

        return SupervisorGUI.lightGui;
    }

    // Variables declaration - do not modify
    private javax.swing.JButton jButton1;

    private javax.swing.JButton jButton2;

    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JSlider jSlider3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JToggleButton jToggleButton2;

    // End of variables declaration
    /** Creates new form LightGUI */
    private SupervisorGUI() {
        super();
        this.initComponents();

        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed"
        // desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase
         * /tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (final javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                    .getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (final ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SupervisorGUI.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (final InstantiationException ex) {
            java.util.logging.Logger.getLogger(SupervisorGUI.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (final IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SupervisorGUI.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (final javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SupervisorGUI.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }

        this.setVisible(true);
    }

    public void addRangeButtonActionListener(final ActionListener l) {
        this.jButton1.addActionListener(l);
        this.jButton2.addActionListener(l);
    }

    public void addSliderChangeListener(final ChangeListener l) {
        this.jSlider3.addChangeListener(l);
    }

    public void addSwitchActionListener(final ActionListener l) {
        this.jToggleButton2.addActionListener(l);
    }

    public String getIntensityValue() {
        return this.jTextField3.getText();
    }

    public String getMaxValue() {
        return this.jTextField2.getText();
    }

    public String getMinValue() {
        return this.jTextField1.getText();
    }

    public String getTimeValue() {
        return this.jTextField4.getText();
    }

    public boolean isConnected() {
        if (this.jLabel11.getText().equals("OK")) {
            return true;
        }

        return false;
    }

    public void setConnectionStatus(final boolean ok) {
        if (ok) {
            this.jPanel7.setBackground(new java.awt.Color(0, 204, 0));
            this.jLabel11.setText("OK");
        } else {
            this.jPanel7.setBackground(new java.awt.Color(255, 0, 0));
            this.jLabel11.setText("BAD");
        }
    }

    public void setIntensityValue(final int value) {
        this.jTextField3.setText(String.valueOf(value));
    }

    public void setLedStatus(final boolean on) {
        if (on) {
            this.jPanel5.setBackground(new java.awt.Color(0, 204, 0));
            this.jLabel9.setText("ON");
        } else {
            this.jPanel5.setBackground(new java.awt.Color(255, 0, 0));
            this.jLabel9.setText("OFF");
        }
    }

    public void setTimeValue(final int value) {
        this.jTextField4.setText(String.valueOf(value));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        final JLabel jLabel1 = new javax.swing.JLabel();
        final JLabel jLabel3 = new javax.swing.JLabel();
        final JPanel jPanel2 = new javax.swing.JPanel();
        this.jSlider3 = new javax.swing.JSlider();
        final JLabel jLabel4 = new javax.swing.JLabel();
        this.jToggleButton2 = new javax.swing.JToggleButton();
        final JPanel jPanel1 = new javax.swing.JPanel();
        this.jTextField1 = new javax.swing.JTextField();
        final JLabel jLabel2 = new javax.swing.JLabel();
        this.jTextField2 = new javax.swing.JTextField();
        final JLabel jLabel5 = new javax.swing.JLabel();
        final JLabel jLabel6 = new javax.swing.JLabel();
        this.jButton1 = new javax.swing.JButton();
        this.jButton2 = new javax.swing.JButton();
        final JLabel jLabel7 = new javax.swing.JLabel();
        final JPanel jPanel3 = new javax.swing.JPanel();
        final JSlider jSlider4 = new javax.swing.JSlider();
        final JLabel jLabel12 = new javax.swing.JLabel();
        final JPanel jPanel4 = new javax.swing.JPanel();
        final JLabel jLabel8 = new javax.swing.JLabel();
        final JLabel jLabel13 = new javax.swing.JLabel();
        this.jTextField3 = new javax.swing.JTextField();
        this.jPanel5 = new javax.swing.JPanel();
        this.jLabel9 = new javax.swing.JLabel();
        final JPanel jPanel6 = new javax.swing.JPanel();
        final JLabel jLabel10 = new javax.swing.JLabel();
        final JLabel jLabel14 = new javax.swing.JLabel();
        this.jTextField4 = new javax.swing.JTextField();
        this.jPanel7 = new javax.swing.JPanel();
        this.jLabel11 = new javax.swing.JLabel();

        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setBackground(new java.awt.Color(0, 204, 204));
        jLabel1.setFont(new java.awt.Font("Tahoma", 3, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 153, 204));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("LIGHT SUPERVISOR");

        jPanel2.setBorder(javax.swing.BorderFactory
                .createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setText("Light's intensity slider");

        this.jToggleButton2.setText("LOCK/UNLOCK");

        jPanel1.setBorder(javax.swing.BorderFactory
                .createLineBorder(new java.awt.Color(0, 0, 0)));

        this.jTextField1.setText("Insert Value");
        this.jTextField1.setName(""); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("INTENSITY RANGE");

        this.jTextField2.setText("Insert value");
        this.jTextField2.setName(""); // NOI18N

        jLabel5.setText("Min Value");

        jLabel6.setText("Max Value");

        this.jButton1.setText("Set Min");

        this.jButton2.setText("Set Max");

        final javax.swing.GroupLayout jPanel1Layout =
                new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout
                .setHorizontalGroup(jPanel1Layout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout
                                        .createSequentialGroup()
                                        .addGroup(
                                                jPanel1Layout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(
                                                                jPanel1Layout
                                                                        .createSequentialGroup()
                                                                        .addGap(38,
                                                                                38,
                                                                                38)
                                                                        .addComponent(
                                                                                jLabel2))
                                                        .addGroup(
                                                                jPanel1Layout
                                                                        .createSequentialGroup()
                                                                        .addContainerGap()
                                                                        .addGroup(
                                                                                jPanel1Layout
                                                                                        .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                false)
                                                                                        .addComponent(
                                                                                                this.jButton1,
                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                0,
                                                                                                0,
                                                                                                Short.MAX_VALUE)
                                                                                        .addComponent(
                                                                                                jLabel5,
                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(
                                                                                                this.jTextField1,
                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                74,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                jPanel1Layout
                                                                                        .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                false)
                                                                                        .addComponent(
                                                                                                this.jButton2,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                        .addComponent(
                                                                                                jLabel6)
                                                                                        .addComponent(
                                                                                                this.jTextField2,
                                                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                74,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addContainerGap(
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));
        jPanel1Layout
                .setVerticalGroup(jPanel1Layout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout
                                        .createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel2)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel1Layout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel5)
                                                        .addComponent(jLabel6))
                                        .addGap(7, 7, 7)
                                        .addGroup(
                                                jPanel1Layout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(
                                                                this.jTextField1,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(
                                                                this.jTextField2,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel1Layout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(
                                                                this.jButton1)
                                                        .addComponent(
                                                                this.jButton2))
                                        .addContainerGap(
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));

        jLabel7.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel7.setText("COMMAND PANEL");

        final javax.swing.GroupLayout jPanel2Layout =
                new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout
                .setHorizontalGroup(jPanel2Layout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                javax.swing.GroupLayout.Alignment.TRAILING,
                                jPanel2Layout.createSequentialGroup()
                                        .addContainerGap(58, Short.MAX_VALUE)
                                        .addComponent(jLabel7)
                                        .addGap(46, 46, 46))
                        .addGroup(
                                jPanel2Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel2Layout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                this.jSlider3,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                179,
                                                                Short.MAX_VALUE)
                                                        .addComponent(jLabel4)
                                                        .addComponent(
                                                                jPanel1,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(
                                                                this.jToggleButton2))
                                        .addContainerGap()));
        jPanel2Layout
                .setVerticalGroup(jPanel2Layout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel2Layout
                                        .createSequentialGroup()
                                        .addGap(24, 24, 24)
                                        .addComponent(jLabel7)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                this.jToggleButton2,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                35,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(
                                                jPanel1,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel4)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(
                                                this.jSlider3,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(27, Short.MAX_VALUE)));

        jPanel3.setBorder(javax.swing.BorderFactory
                .createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel12.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel12.setText("CONTROL PANEL");

        jPanel4.setBorder(javax.swing.BorderFactory
                .createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel8.setText("Intensity");

        jLabel13.setFont(new java.awt.Font("Tahoma", 3, 11));
        jLabel13.setText("Light Status");

        this.jTextField3.setEditable(false);

        this.jPanel5.setBackground(new java.awt.Color(255, 0, 0));
        this.jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        this.jPanel5.setForeground(new java.awt.Color(255, 255, 255));
        this.jPanel5.setMaximumSize(new java.awt.Dimension(100, 100));
        this.jPanel5.setPreferredSize(new java.awt.Dimension(40, 40));

        this.jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
        this.jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        this.jLabel9.setText("OFF");

        final javax.swing.GroupLayout jPanel5Layout =
                new javax.swing.GroupLayout(this.jPanel5);
        this.jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                jPanel5Layout.createSequentialGroup()
                        .addContainerGap(16, Short.MAX_VALUE)
                        .addComponent(this.jLabel9)));
        jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                jPanel5Layout.createSequentialGroup()
                        .addContainerGap(22, Short.MAX_VALUE)
                        .addComponent(this.jLabel9)));

        final javax.swing.GroupLayout jPanel4Layout =
                new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout
                .setHorizontalGroup(jPanel4Layout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel4Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel4Layout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel13)
                                                        .addGroup(
                                                                jPanel4Layout
                                                                        .createSequentialGroup()
                                                                        .addGroup(
                                                                                jPanel4Layout
                                                                                        .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                false)
                                                                                        .addComponent(
                                                                                                this.jTextField3,
                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(
                                                                                                jLabel8,
                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE))
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                62,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                this.jPanel5,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addContainerGap()));
        jPanel4Layout
                .setVerticalGroup(jPanel4Layout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel4Layout
                                        .createSequentialGroup()
                                        .addGap(11, 11, 11)
                                        .addComponent(jLabel13)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(
                                                jPanel4Layout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                this.jPanel5,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .addGroup(
                                                                jPanel4Layout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                jLabel8)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                this.jTextField3,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addContainerGap()));

        jPanel6.setBorder(javax.swing.BorderFactory
                .createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setText("Time");

        jLabel14.setFont(new java.awt.Font("Tahoma", 3, 11));
        jLabel14.setText("Connection Status");

        this.jTextField4.setEditable(false);

        this.jPanel7.setBackground(new java.awt.Color(255, 0, 0));
        this.jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        this.jPanel7.setForeground(new java.awt.Color(255, 255, 255));
        this.jPanel7.setMaximumSize(new java.awt.Dimension(100, 100));
        this.jPanel7.setPreferredSize(new java.awt.Dimension(40, 40));

        this.jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11));
        this.jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        this.jLabel11.setText("BAD");

        final javax.swing.GroupLayout jPanel7Layout =
                new javax.swing.GroupLayout(this.jPanel7);
        this.jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(jPanel7Layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                jPanel7Layout.createSequentialGroup()
                        .addContainerGap(13, Short.MAX_VALUE)
                        .addComponent(this.jLabel11)));
        jPanel7Layout.setVerticalGroup(jPanel7Layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                jPanel7Layout.createSequentialGroup()
                        .addContainerGap(22, Short.MAX_VALUE)
                        .addComponent(this.jLabel11)));

        final javax.swing.GroupLayout jPanel6Layout =
                new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout
                .setHorizontalGroup(jPanel6Layout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel6Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel6Layout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                jLabel14,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                145,
                                                                Short.MAX_VALUE)
                                                        .addGroup(
                                                                jPanel6Layout
                                                                        .createSequentialGroup()
                                                                        .addGroup(
                                                                                jPanel6Layout
                                                                                        .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(
                                                                                                jLabel10)
                                                                                        .addComponent(
                                                                                                this.jTextField4,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                76,
                                                                                                Short.MAX_VALUE))
                                                                        .addGap(29,
                                                                                29,
                                                                                29)
                                                                        .addComponent(
                                                                                this.jPanel7,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addContainerGap()));
        jPanel6Layout
                .setVerticalGroup(jPanel6Layout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel6Layout
                                        .createSequentialGroup()
                                        .addGap(11, 11, 11)
                                        .addComponent(jLabel14)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel6Layout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(
                                                                jPanel6Layout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                jLabel10)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                this.jTextField4,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(
                                                                this.jPanel7,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE))
                                        .addContainerGap()));

        final javax.swing.GroupLayout jPanel3Layout =
                new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout
                .setHorizontalGroup(jPanel3Layout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel3Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel3Layout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(
                                                                jPanel3Layout
                                                                        .createSequentialGroup()
                                                                        .addGroup(
                                                                                jPanel3Layout
                                                                                        .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(
                                                                                                jSlider4,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                0,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(
                                                                                                jPanel4,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                        .addComponent(
                                                                                                jPanel6,
                                                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE))
                                                                        .addContainerGap())
                                                        .addGroup(
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                jPanel3Layout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                jLabel12)
                                                                        .addGap(47,
                                                                                47,
                                                                                47)))));
        jPanel3Layout
                .setVerticalGroup(jPanel3Layout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel3Layout
                                        .createSequentialGroup()
                                        .addGap(24, 24, 24)
                                        .addComponent(jLabel12)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                jPanel4,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(
                                                jPanel6,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(40, 40, 40)
                                        .addComponent(
                                                jSlider4,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));

        final javax.swing.GroupLayout layout =
                new javax.swing.GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                        layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel3)
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(
                                                        jLabel1,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        396, Short.MAX_VALUE)
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addComponent(
                                                                        jPanel2,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(
                                                                        jPanel3,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE)))
                                .addContainerGap()));
        layout.setVerticalGroup(layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        layout.createSequentialGroup()
                                .addComponent(jLabel1,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        33,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addGap(38, 38,
                                                                        38)
                                                                .addComponent(
                                                                        jLabel3))
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(
                                                                        layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(
                                                                                        jPanel2,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        jPanel3,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        281,
                                                                                        Short.MAX_VALUE))))
                                .addContainerGap()));

        this.pack();
    }// </editor-fold>
}
