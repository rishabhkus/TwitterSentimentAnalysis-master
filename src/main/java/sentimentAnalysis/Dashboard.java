package sentimentAnalysis;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rogue1
 */
import java.util.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.TwitterFactory;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
public class Dashboard extends javax.swing.JFrame {
    InputStream fis = null;
    int countn=0,countp=0,neutral=0;
    User targetUser = null;
    
    static String consumer_key="<consumer_key>",consumer_secret="<consumer_secret>",
            access_token="<access_token>",access_token_secret="<access_secret>";
    
   
    String user="";
    //function to add tweets to text area
    private void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        int len = tp.getDocument().getLength();
        tp.setCharacterAttributes(aset, false);
        tp.setCaretPosition(len);
        tp.replaceSelection(msg);
    }
    void offlineTweet() throws ClassNotFoundException{       
        
        try {Class.forName("org.sqlite.JDBC");
            Connection con=DriverManager.getConnection("jdbc:sqlite:hi.db");
            Statement stmt=con.createStatement();
            
            ResultSet rs;
            rs = stmt.executeQuery("select * from tweeter order by id desc");
            stmt.close();
             while(rs.next())
            {
                appendToPane(jTextArea1,rs.getString("name")+": ",Color.BLUE);
                appendToPane(jTextArea1,rs.getString("text")+"\n",Color.BLACK);
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
           
                    
        
                    
        
    }
    
     void Tweeter() throws FileNotFoundException, ClassNotFoundException, SQLException {
         Connection con=null;
         Statement stmt=null;
         ResultSet rs=null;
         
        try {
            Class.forName("org.sqlite.JDBC");
             con=DriverManager.getConnection("jdbc:sqlite:hi.db");
            stmt=con.createStatement();                
            rs = stmt.executeQuery("SELECT MAX(id) FROM tweeter;");
            
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumer_key)
                .setOAuthConsumerSecret(consumer_secret)
                .setOAuthAccessToken(access_token )
                .setOAuthAccessTokenSecret( access_token_secret );
            
            TwitterFactory tf = new TwitterFactory(cb.build());
            twitter4j.Twitter twitter = tf.getInstance();
            
            List<Status> status;
            status = twitter.getHomeTimeline();
           
            Long MaxId=rs.getLong(1);
            
                       
            con.close();
            System.gc();
            
            if(status.get(0).getId()==MaxId)
            {   con=DriverManager.getConnection("jdbc:sqlite:hi.db");
                stmt=con.createStatement();
                rs = stmt.executeQuery("select * from tweeter order by id desc");
                
                
             while(rs.next())
            {
                appendToPane(jTextArea1,rs.getString("name")+": ",Color.BLUE);
                appendToPane(jTextArea1,rs.getString("text")+"\n",Color.BLACK);
                
            }
                JOptionPane.showMessageDialog(this,"Database Already Updated");
                stmt.close();
                con.close();
                System.gc();
                }
            else{
                
           for(int i=0;i<status.size();i++){
               if(status.get(i).getId()==MaxId)
                   break;
               String txt=status.get(i).getText();
               
        txt = txt.replaceAll("(?m)^[ \t]*\r?\n", "");        //removes the empty lines or spaces
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(txt);
        int j = 0;
        while (m.find()) {
            txt = txt.replaceAll(m.group(j),"").trim();
            j++;
        }
                     
            try {
                    //PreparedStatement statement=con.prepareStatement("insert into tweeter(id,text,name,analysis) values(?,?,?,'0')"); 
        //statement.setLong(1, status.get(i).getId());
        //statement.setString(2, txt);
        //statement.setString(3, status.get(i).getUser().getName());
        //statement.executeUpdate();
                         //making entry in database
                         System.gc();
                         con=DriverManager.getConnection("jdbc:sqlite:hi.db");
                         stmt=con.createStatement();
                         stmt.executeUpdate("insert into tweeter(id,text,name,analysis) values("+status.get(i).getId()+",'"+txt+"','"+status.get(i).getUser().getName()+"',"+"'0');");
                         stmt.close();
                         con.close();
                         System.gc();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    JOptionPane.showMessageDialog(this,ex.getMessage()+"One");
                    ex.printStackTrace();
                }}
           con=DriverManager.getConnection("jdbc:sqlite:hi.db");
            stmt=con.createStatement();
           rs = stmt.executeQuery("select * from tweeter order by id desc");
           
           while(rs.next())
            {
                appendToPane(jTextArea1,rs.getString("name")+": ",Color.BLUE);
                appendToPane(jTextArea1,rs.getString("text")+"\n",Color.BLACK);
                
            }
           JOptionPane.showMessageDialog(this,"Tweet extraction Done!");
           stmt.close();
           con.close();
           System.gc();

        } } catch (TwitterException ex) {JOptionPane.showMessageDialog(this, "You aren't connected to internet/twitter.");
            int ans;
            ans=JOptionPane.showConfirmDialog(this, "Do you want to use offline saved tweets ?");
            if(ans==0){
                System.gc();
                con=DriverManager.getConnection("jdbc:sqlite:hi.db");
               stmt=con.createStatement();
           rs = stmt.executeQuery("select * from tweeter order by id desc");
           
             while(rs.next())
            {
                appendToPane(jTextArea1,rs.getString("name")+": ",Color.BLUE);
                appendToPane(jTextArea1,rs.getString("text")+"\n",Color.BLACK);
                
            }
            }
             
           stmt.close();
           con.close();
           System.gc();
        }
    }
     
    
    public Dashboard() throws SQLException {
        initComponents();
         
       
        
        ConfigurationBuilder cb = new ConfigurationBuilder();
            
            cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumer_key)
                .setOAuthConsumerSecret(consumer_secret)
                .setOAuthAccessToken(access_token)
                .setOAuthAccessTokenSecret( access_token_secret );
            
            TwitterFactory tf = new TwitterFactory(cb.build());
            twitter4j.Twitter twitter = tf.getInstance();
       
        user =   "Rick63682684"; 
        try{
            targetUser  = twitter.showUser(user);
            targetUser  = twitter.showUser(user);
            followers.setText(targetUser.getFollowersCount()+"");
            following.setText(targetUser.getFriendsCount()+"");
            tweets.setText(targetUser.getStatusesCount()+"");
            user_name.setText(user); }
        
        catch(Exception e){
            System.out.println(e.getMessage());}}
    
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        user_name = new javax.swing.JLabel();
        tweets = new javax.swing.JLabel();
        following = new javax.swing.JLabel();
        followers = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        positive_count = new javax.swing.JLabel();
        neutral_count = new javax.swing.JLabel();
        negative_count = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        file = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Twitter Sentiment Analyzer");
        setBackground(new java.awt.Color(0, 0, 204));

        jPanel1.setBackground(new java.awt.Color(0, 51, 102));

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Analysis");
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Tweets");

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Following");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Followers");

        user_name.setFont(new java.awt.Font("Lucida Grande", 2, 13)); // NOI18N
        user_name.setForeground(new java.awt.Color(102, 204, 255));
        user_name.setText("@ twitter_handle");

        tweets.setForeground(new java.awt.Color(102, 204, 255));
        tweets.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        tweets.setText("0");

        following.setForeground(new java.awt.Color(102, 204, 255));
        following.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        following.setText("0");

        followers.setForeground(new java.awt.Color(102, 204, 255));
        followers.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        followers.setText("0");

        jLabel5.setForeground(new java.awt.Color(0, 153, 153));
        jLabel5.setText("Positive");

        jLabel6.setForeground(new java.awt.Color(0, 255, 255));
        jLabel6.setText("Neutral");

        positive_count.setForeground(new java.awt.Color(102, 204, 255));
        positive_count.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        positive_count.setText("0");

        neutral_count.setForeground(new java.awt.Color(102, 204, 255));
        neutral_count.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        neutral_count.setText("0");

        negative_count.setForeground(new java.awt.Color(102, 204, 255));
        negative_count.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        negative_count.setText("0");

        jLabel8.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Username");

        jLabel7.setForeground(new java.awt.Color(204, 0, 0));
        jLabel7.setText("Negative");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(48, 48, 48)
                        .addComponent(neutral_count, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(user_name)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(followers))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(following))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tweets))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(46, 46, 46)
                        .addComponent(positive_count, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(48, 48, 48)
                        .addComponent(negative_count, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel1))
                    .addComponent(jLabel8))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(user_name)
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tweets))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(following))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(followers))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(positive_count, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(neutral_count))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(negative_count)
                    .addComponent(jLabel7))
                .addGap(34, 34, 34))
        );

        jPanel4.setBackground(new java.awt.Color(0, 0, 51));

        jButton1.setText("Get Tweets");
        jButton1.setToolTipText("Click to get the updated dashboard");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Analyze");
        jButton2.setToolTipText("Click to obtain sentiment analysis");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Mood Graph");
        jButton3.setToolTipText("Click to obtain your mood graph");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("New Tweet");
        jButton4.setToolTipText("Wanna tweet ?");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Search");
        jButton5.setToolTipText("Click to obtain your mood graph");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 43, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(54, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );

        jTextArea1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 804, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        jMenuBar1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        file.setText("File");
        file.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N

        jMenuItem3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem3.setText("Export");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        file.add(jMenuItem3);

        jMenuItem4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem4.setText("Exit");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        file.add(jMenuItem4);

        jMenuBar1.add(file);

        jMenu2.setText("Settings");
        jMenu2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N

        jMenuItem1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem1.setText("Reset");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        
            // TODO add your handling code here:
        TweetUpdate tu = new TweetUpdate();    
        tu.setVisible(true);
        
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        MoodGraph mu = new MoodGraph(countp,countn,neutral);
        mu.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            countp=0;countn=0;neutral=0;
            SQLData in=new SQLData();
            ResultSet rs=null;
            Statement stmt=in.stmt;
            for(int i=0;i==0;i++){
                try {
                    
                    
                    this.jTextArea1.setText("");
                    String tweet;
                    
                    rs=stmt.executeQuery("select * from tweeter where analysis='0'");
                    if(!rs.isBeforeFirst()){JOptionPane.showMessageDialog(this,"Breaking loop");break;}
                    HashMap<Long,String> hs=new HashMap();
                    while(rs.next())
                    {
                        hs.put(rs.getLong(1),rs.getString(2).toString());
                    }
                    
                    rs=in.tableData();
                    for(Map.Entry m:hs.entrySet()){
                        String arr[]=SentimentAnalysis.main(new String[]{m.getValue().toString()});              //Analysis Done
                        stmt.executeUpdate("UPDATE tweeter set analysis='"+arr[6]+"',vp="+arr[0]+",p="+arr[1]+",nu="+arr[2]+",n="+arr[3]+",vn="+arr[4]+" WHERE id="+m.getKey());     //Saving Responses
                        
                    }
                    
                    rs=in.tableData();
                    while(rs.next()){
                        
                        String arr[]=SentimentAnalysis.main(new String[]{rs.getString("text")});
                        
                        
                        appendToPane(jTextArea1, rs.getString("name")+": ", Color.blue);
                        appendToPane(jTextArea1, rs.getString("text"), Color.BLACK);
                        this.jTextArea1.setForeground(Color.red);
                        if(arr[6].equals("Negative")){
                            appendToPane(jTextArea1, "\nSentiment : NEGATIVE "+arr[3]+"%N "+arr[4]+"%VN"+"\n", Color.RED);
                            //  jTextArea1.append("Sentiment : NEGATIVE\n");
                            countn++;
                        }
                        else if(arr[6].equals("Positive")){
                            appendToPane(jTextArea1, "\nSentiment : POSITIVE "+arr[0]+"% VP "+arr[1]+"% P"+"\n", Color.GREEN);
                            // jTextArea1.append("Sentiment : Positive\n");
                            countp++;
                        }
                        else {
                            appendToPane(jTextArea1, "\nSentiment : NEUTRAL "+arr[2]+"%N "+"\n", Color.CYAN);
                            // jTextArea1.append("Sentiment : Neutral\n");
                            neutral++;
                        }
                        this.jTextArea1.setForeground(Color.black);
                        
                        
                        
                        positive_count.setText(countp+"");
                        negative_count.setText(countn+"");
                        neutral_count.setText(neutral+"");
                        
                    }} catch (Exception ex) {
                        
                        JOptionPane.showMessageDialog(this,ex.getMessage());
                        Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
        
        rs=in.tableData();
        while(rs.next()){
                        
                       
                        
                        
                        appendToPane(jTextArea1, rs.getString("name")+": ", Color.blue);
                        appendToPane(jTextArea1, rs.getString("text"), Color.BLACK);
                        this.jTextArea1.setForeground(Color.red);
                        if(rs.getString(4).equals("Negative")||rs.getString(4).equals("Very Negative")){
                            appendToPane(jTextArea1, "\nSentiment : NEGATIVE "+rs.getString(8)+"%N "+rs.getString(9)+"%VN"+"\n", Color.RED);
                            //  jTextArea1.append("Sentiment : NEGATIVE\n");
                            countn++;
                        }
                        else if(rs.getString(4).equals("Positive")||rs.getString(4).equals("Very Positive")){
                            appendToPane(jTextArea1, "\nSentiment : POSITIVE "+rs.getString(5)+"% VP "+rs.getString(6)+"% P"+"\n", Color.GREEN);
                            // jTextArea1.append("Sentiment : Positive\n");
                            countp++;
                        }
                        else {
                            appendToPane(jTextArea1, "\nSentiment : NEUTRAL "+rs.getString(7)+"%Neutral "+"\n", Color.CYAN);
                            // jTextArea1.append("Sentiment : Neutral\n");
                            neutral++;
                        }
                        this.jTextArea1.setForeground(Color.black);
                        
                        
                        
                        positive_count.setText(countp+"");
                        negative_count.setText(countn+"");
                        neutral_count.setText(neutral+"");
        }
        
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        
        String search=JOptionPane.showInputDialog(this,"Enter the keystring to search",JOptionPane.OK_OPTION);
        if(search!=null){       countp=0;countn=0;neutral=0;
        ConfigurationBuilder cb = new ConfigurationBuilder();
            
            cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumer_key)
                .setOAuthConsumerSecret(consumer_secret)
                .setOAuthAccessToken(access_token )
                .setOAuthAccessTokenSecret( access_token_secret );
            
            TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        try {
            Query query = new Query(search);
            QueryResult result;
            
            result = twitter.search(query);
            List<Status> status = result.getTweets();
            jTextArea1.setText("");
            status.stream().forEach((Status st) -> {
                String txt=st.getText();
                txt = txt.replaceAll("(?m)^[ \t]*\r?\n", "");        //removes the empty lines or spaces
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(txt);
        int j = 0;
        while (m.find()) {
            txt = txt.replaceAll(m.group(j),"").trim();
            j++;
        }
                
                 appendToPane(jTextArea1, st.getUser().getName()+": ", Color.BLUE);
                  appendToPane(jTextArea1,txt+"\n",Color.BLACK);
                  
                  
                   String arr[]=SentimentAnalysis.main(new String[]{txt});
                   if(arr[6].equals("Negative")){
                    appendToPane(jTextArea1, "\nSentiment : NEGATIVE "+arr[3]+"%N "+arr[4]+"%VN"+"\n", Color.RED);
                   //  jTextArea1.append("Sentiment : NEGATIVE\n");
                    countn++;
                }
                else if(arr[6].equals("Positive")){
                    appendToPane(jTextArea1, "\nSentiment : POSITIVE "+arr[0]+"% VP "+arr[1]+"% P"+"\n", Color.GREEN);
                    // jTextArea1.append("Sentiment : Positive\n");
                    countp++;
                }
                else {
                    appendToPane(jTextArea1, "\nSentiment : NEUTRAL "+arr[2]+"%N "+"\n", Color.CYAN);
                    // jTextArea1.append("Sentiment : Neutral\n");
                    neutral++;
                }
                   
            });
           
            positive_count.setText(countp+"");
            negative_count.setText(countn+"");
            neutral_count.setText(neutral+"");
        

        
        }catch (TwitterException te) {
           
            System.out.println("Failed to search tweets: " + te.getMessage());}}
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.jTextArea1.setText("");
        try {
            try {
                Tweeter();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        export.main(new String[]{});
        JOptionPane.showMessageDialog(this,"Data has been exported in the form of Excel Sheet");
        
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        // TODO add your handling code here:
        new tweetTable();
    }//GEN-LAST:event_jLabel1MouseClicked

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here
        System.exit(0);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
      
            // TODO add your handling code here:
            int ans=JOptionPane.showConfirmDialog(this, "Do you wish to delete all saved data ?");
            if(ans==0){  
                try {
                SQLData data=new SQLData();
            data.reset();
                        JOptionPane.showMessageDialog(this,"Data Deletion Completed");

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
            }
            else{
            JOptionPane.showMessageDialog(this,"Data Deletion Cancelled.");}
            
        
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws TwitterException {
         /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        try {
            //</editor-fold>
            new Dashboard().setVisible(true);
            
            
            /* Create and display the form */
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu file;
    private javax.swing.JLabel followers;
    private javax.swing.JLabel following;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextArea1;
    private javax.swing.JLabel negative_count;
    private javax.swing.JLabel neutral_count;
    private javax.swing.JLabel positive_count;
    private javax.swing.JLabel tweets;
    private javax.swing.JLabel user_name;
    // End of variables declaration//GEN-END:variables

    private static class NewTweet {

        public NewTweet() {
        }
    }
}
