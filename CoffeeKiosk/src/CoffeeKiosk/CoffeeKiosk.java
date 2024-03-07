package CoffeeKiosk;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class CoffeeKiosk {
   private KioskFrame kiosk;
   private PosPanel pos;
   public int delivery = 6; // 배달 소요시간은 6분으로 가정

   public CoffeeKiosk() {
        // PosPanel을 KioskFrame에 할당
        SwingUtilities.invokeLater(() -> {
            kiosk = new KioskFrame(pos);
            pos = new PosPanel(new POSFrame(kiosk), kiosk);

            kiosk.setPosPanel(pos);
        });
    }

   public class totalTimeQueue {
      private LinkedList<Integer> queue;

      public totalTimeQueue() {
         queue = new LinkedList<>();
      }

      // 큐에 요소 추가
      public void enqueue(int item) {
         queue.addLast(item);
      }

      // 큐에서 요소 제거
      public void dequeue() {
         if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
         }
         queue.removeFirst();
      }

      // 큐의 맨 앞 요소 반환
      public int peek() {
         if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
         }
         return queue.getFirst();
      }

      // 큐의 크기 반환
      public int size() {
         return queue.size();
      }

      // 큐가 비어 있는지 확인
      public boolean isEmpty() {
         return queue.isEmpty();
      }

      // 큐에 있는 모든 요소의 합을 계산
      public int calculateSum() {
         int sum = 0;
         for (int element : queue) {
            sum += element;
         }
         return sum;
      }

      // 큐에서 값의 인덱스 반환 (없으면 -1)
      public int indexOf(int value) {
         return queue.indexOf(value);
      }

      // 큐에서 특정 인덱스의 요소를 삭제하면서 순서를 유지
      public void removeElementAtIndex(int index) {
         if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Invalid index");
         }

         // 인덱스 이전까지의 요소를 큐에서 빼서 저장
         LinkedList<Integer> tempQueue = new LinkedList<>();
         for (int i = 0; i < index; i++) {
            tempQueue.addLast(queue.removeFirst());
         }

         // 삭제할 인덱스의 요소를 무시하고 큐에서 빼고
         queue.removeFirst();

         // 나머지 요소를 다시 큐에 추가
         while (!tempQueue.isEmpty()) {
            queue.addLast(tempQueue.removeFirst());
         }

         // 기존 큐에 남아있는 요소를 마저 tempQueue에 넣고 다시 큐로 넣어서 순서를 유지
         while (!queue.isEmpty()) {
            tempQueue.addLast(queue.removeFirst());
         }

         // tempQueue의 요소를 다시 큐로 옮김
         while (!tempQueue.isEmpty()) {
            queue.addLast(tempQueue.removeFirst());
         }
      }
   }

   public class StartPanel extends JPanel {
      private ImageIcon start = new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/Character/시작 화면.png");
      private JLabel startImg = new JLabel(start);
      private OrderPanel order;
      private CategoryPanel category;
      private MenuPanel menu;
      private KioskFrame kiosk;
      private Clip clip;
      public int ifClicked = -1;

      public StartPanel(KioskFrame kiosk) {
         this.kiosk = kiosk;
         setLayout(null);
         startImg.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
               ifClicked = 1;
               kiosk.checkIfClicked(ifClicked);
            }
         });

         add(startImg);
         startImg.setBounds(0, 0, 500, 700);
         // startImg.setBackground(Color.white);

         loadAudio("C:/강지후/객체지향언어2/과제/src/JavaCoffee/audio/selection.wav");
      }

      public void loadAudio(String pathName) {
         try {
            clip = AudioSystem.getClip(); // 비어있는 오디오 클립 만들기
            File audioFile = new File(pathName); // 오디오 파일의 경로명
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile); // 오디오 파일로부터
            clip.open(audioStream); // 재생할 오디오 스트림 열기
         } catch (LineUnavailableException e) {
            e.printStackTrace();
         } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }

      }
   }

   public class CategoryPanel extends JPanel {
      private JButton btn;
      private JTextArea txtArea;
      private KioskFrame kiosk;
      private MenuPanel menu;
      private JLabel brand = new JLabel("JAVA COFFEE");
      private JButton[] category = new JButton[4];
      public String cate = "커피(ICE)";
      private Clip clip;
      public JLabel orderCnt; // 앞선 주문 건수
      public JLabel duration; // 총 소요 시간
      private ImageIcon Img = new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/Character/미니언즈 중간 사진.jpg");
      private JLabel centerImg = new JLabel(Img);

      private List<String> quotes;
      private JLabel quoteLabel1; // 예시로 두 개의 JLabel이 있다고 가정

      public CategoryPanel(KioskFrame kiosk) {
         this.kiosk = kiosk;
         this.menu = new MenuPanel(kiosk, this);

         setLayout(null);

         // 화면구성
         brand.setSize(180, 30);
         brand.setLocation(160, 5);
         brand.setForeground(Color.DARK_GRAY);
         brand.setFont(new Font("Arial", Font.BOLD, 25));

         add(brand);

         orderCnt = new JLabel("앞선 주문 건수: " + kiosk.orderCount);
         orderCnt.setSize(100, 15);
         orderCnt.setLocation(360, 7);
         orderCnt.setFont(new Font("Malgun Gothic", Font.BOLD, 9));

         add(orderCnt);

         duration = new JLabel("앞선 주문 소요 시간: " + kiosk.timeQ.calculateSum() + "분");
         duration.setSize(150, 15);
         duration.setLocation(360, 17);
         duration.setFont(new Font("Malgun Gothic", Font.BOLD, 9));

         add(duration);

         category[0] = new JButton("커피(ICE)");
         category[1] = new JButton("커피(HOT)");
         category[2] = new JButton("차(TEA)");
         category[3] = new JButton("음료(BEV)");

         for (int i = 0; i < category.length; i++) {
            category[i].addActionListener(new CategoryActionListener());
            category[i].setSize(100, 30);
            category[i].setLocation(15 + 120 * i, 40);
            category[i].setOpaque(true);
            category[i].setBackground(Color.lightGray);
            category[i].setForeground(Color.black);
            category[i].setFont(new Font("Malgun Gothic", Font.BOLD, 10));
            add(category[i]);
         }

         // 명언을 읽어옴
         quotes = loadQuotesFromFile("C:/강지후/객체지향언어2/과제/src/JavaCoffee/quotes/quotes.txt");

         // 기존 코드에서 이미 생성된 JLabel 객체를 참조
         quoteLabel1 = new JLabel("Initial Text for Label 1");
         add(quoteLabel1);
         quoteLabel1.setFont(new Font("Malgun Gothic", Font.BOLD, 10));
         quoteLabel1.setBounds(20, 70, 500, 30);

         // GUI 창이 열릴 때마다 랜덤 명언을 표시
         displayRandomQuote();

         centerImg.setBounds(100, 90, 300, 60);

         add(centerImg);

         setOpaque(true);
         setVisible(true);

         loadAudio("./audio/selection.wav");
      }

      public void loadAudio(String pathName) {
         try {
            clip = AudioSystem.getClip(); // 비어있는 오디오 클립 만들기
            File audioFile = new File(pathName); // 오디오 파일의 경로명
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile); // 오디오 파일로부터
            clip.open(audioStream); // 재생할 오디오 스트림 열기
         } catch (LineUnavailableException e) {
            e.printStackTrace();
         } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }

      }

      private List<String> loadQuotesFromFile(String filePath) {
         List<String> quotesList = new ArrayList<>();
         try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
               // " "로 구분된 각 문장을 리스트에 추가
               String[] sentences = line.split("\n");
               for (String sentence : sentences) {
                  quotesList.add(sentence);
               }
            }
         } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading quotes from file: " + e.getMessage());
         }
         return quotesList;
      }

      private void displayRandomQuote() {
         if (!quotes.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(quotes.size());
            String randomQuote = quotes.get(randomIndex);

            // 기존에 참조한 JLabel에 명언을 표시
            quoteLabel1.setText("오늘의 명언 : " + randomQuote);
            System.out.println(randomQuote);
         }

         else {
            quoteLabel1.setText("No quotes available.");
         }
      }

      public void displayOrderCount() {

      }

      public void displayOrderEstimateTime() {
         // 예상 소요 시간 띄우기
      }

      class CategoryActionListener implements ActionListener {
         public void actionPerformed(ActionEvent e) {
            // 임시로 change로 채우기
            clip.start();
            clip.setFramePosition(0);
            JButton clickedButton = (JButton) e.getSource();
            cate = clickedButton.getText();

            // 카테고리 버튼이 눌렸을 때 Menu판넬이 바뀌는 동작
            kiosk.getMenuPanel().changeMenu(cate);

         }
      }
   }

   public class MenuPanel extends JPanel {
      private JButton btn;
      private KioskFrame kiosk;
      private CategoryPanel category;
      private OrderPanel order;
      private JButton[] menus = new JButton[8];
      private Clip clip;
      private ImageIcon[] IceImg = { new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/IceMenuImages/아메리카노.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/IceMenuImages/카페라떼.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/IceMenuImages/연유라떼.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/IceMenuImages/모카라떼.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/IceMenuImages/콜드브루.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/IceMenuImages/콜드브루 라떼.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/IceMenuImages/카라멜 라떼.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffeeImages/IceMenuImages/시나몬 라떼.jpg") };
      private ImageIcon[] HotImg = { new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/HotMenuImages/아메리카노.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/HotMenuImages/카페라떼.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/HotMenuImages/연유라떼.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/HotMenuImages/카페모카.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/HotMenuImages/카푸치노.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/HotMenuImages/티라미수 라떼.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/HotMenuImages/카라멜마끼아또.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/HotMenuImages/바닐라라떼.jpg") };
      private ImageIcon[] TeaImg = { new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/TeaMenuImages/녹차.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/TeaMenuImages/레몬차.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/TeaMenuImages/사과유자차.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/TeaMenuImages/얼그레이.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/TeaMenuImages/페퍼민트.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/TeaMenuImages/유자차.jpg"),
            new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/TeaMenuImages/자몽차.jpg"),
            new ImageIcon("./Images/TeaMenuImages/캐모마일.jpg") };
      private ImageIcon[] BevImg = { new ImageIcon("./Images/BevMenuImages/고구마라떼.jpg"),
            new ImageIcon("./Images/BevMenuImages/곡물라떼.jpg"),
            new ImageIcon("./Images/BevMenuImages/녹차라떼.jpg"),
            new ImageIcon("./Images/BevMenuImages/딸기라떼.jpg"),
            new ImageIcon("./Images/BevMenuImages/밀크티라떼.jpg"),
            new ImageIcon("./Images/BevMenuImages/아이스초코.jpg"),
            new ImageIcon("./Images/BevMenuImages/토피넛라떼.jpg"),
            new ImageIcon("./Images/BevMenuImages/흑당버블티.jpg") };;

      public void loadAudio(String pathName) {
         try {
            clip = AudioSystem.getClip(); // 비어있는 오디오 클립 만들기
            File audioFile = new File(pathName); // 오디오 파일의 경로명
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile); // 오디오 파일로부터
            clip.open(audioStream); // 재생할 오디오 스트림 열기
         } catch (LineUnavailableException e) {
            e.printStackTrace();
         } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }

      }

      public MenuPanel(KioskFrame kiosk, CategoryPanel category) {
         this.kiosk = kiosk;
         this.category = category;

         setLayout(new GridBagLayout());
         GridBagConstraints gbc = new GridBagConstraints();

         menus[0] = new JButton("<html><center>아메리카노<br>2,000원</center></html>", IceImg[0]);
         menus[1] = new JButton("<html><center>카페라떼<br>2,900원</center></html>", IceImg[1]);
         menus[2] = new JButton("<html><center>연유라떼<br>3,900원</center></html>", IceImg[2]);
         menus[3] = new JButton("<html><center>모카라떼<br>3,700원</center></html>", IceImg[3]);
         menus[4] = new JButton("<html><center>콜드브루<br>3,000원</center></html>", IceImg[4]);
         menus[5] = new JButton("<html><center>콜드브루라떼<br>3,900원</center></html>", IceImg[5]);
         menus[6] = new JButton("<html><center>카라멜라떼<br>3,700원</center></html>", IceImg[6]);
         menus[7] = new JButton("<html><center>시나몬라떼<br>3,700원</center></html>", IceImg[7]);

         for (int i = 0; i < menus.length; i++) {
            customizeButton(menus[i]);

            menus[i].addActionListener(new MenuActionListener());
            menus[i].setPreferredSize(new Dimension(300, 300)); // 원하는 크기로 변경

            gbc.gridx = i % 4;
            gbc.gridy = i / 4;
            gbc.fill = GridBagConstraints.BOTH;

            add(menus[i], gbc);
         }

         loadAudio("./audio/selection.wav");
      }

      private void customizeButton(JButton button) {
         button.setBackground(Color.white);
         button.setFont(new Font("Malgun Gothic", Font.BOLD, 9));
         button.setHorizontalAlignment(SwingConstants.CENTER);
         button.setVerticalTextPosition(JButton.BOTTOM);
         button.setHorizontalTextPosition(JButton.CENTER);
      }

      public void changeMenu(String ca) {
         removeAll();
         setLayout(new GridBagLayout());
         GridBagConstraints gbc = new GridBagConstraints();

         if (ca.equals("커피(ICE)")) {
            // 각각 버튼 구성
            menus[0] = new JButton("<html><center>아메리카노<br>2,000원</center></html>", IceImg[0]);
            menus[1] = new JButton("<html><center>카페라떼<br>2,900원</center></html>", IceImg[1]);
            menus[2] = new JButton("<html><center>연유라떼<br>3,900원</center></html>", IceImg[2]);
            menus[3] = new JButton("<html><center>모카라떼<br>3,700원</center></html>", IceImg[3]);
            menus[4] = new JButton("<html><center>콜드브루<br>3,000원</center></html>", IceImg[4]);
            menus[5] = new JButton("<html><center>콜드브루라떼<br>3,900원</center></html>", IceImg[5]);
            menus[6] = new JButton("<html><center>카라멜라떼<br>3,700원</center></html>", IceImg[6]);
            menus[7] = new JButton("<html><center>시나몬라떼<br>3,700원</center></html>", IceImg[7]);

         }

         else if (ca.equals("커피(HOT)")) {
            menus[0] = new JButton("<html><center>아메리카노<br>2,000원</center></html>", HotImg[0]);
            menus[1] = new JButton("<html><center>카페라떼<br>2,900원</center></html>", HotImg[1]);
            menus[2] = new JButton("<html><center>연유라떼<br>3,900원</center></html>", HotImg[2]);
            menus[3] = new JButton("<html><center>카페모카<br>3,900원</center></html>", HotImg[3]);
            menus[4] = new JButton("<html><center>카푸치노<br>3,700원</center></html>", HotImg[4]);
            menus[5] = new JButton("<html><center>티라미수라떼<br>3,900원</center></html>", HotImg[5]);
            menus[6] = new JButton("<html><center>카라멜라떼<br>3,900원</center></html>", HotImg[6]);
            menus[7] = new JButton("<html><center>바닐라라떼<br>3,700원</center></html>", HotImg[7]);

         }

         else if (ca.equals("차(TEA)")) {
            menus[0] = new JButton("<html><center>녹차<br>2,500원</center></html>", TeaImg[0]);
            menus[1] = new JButton("<html><center>레몬차<br>2,500원</center></html>", TeaImg[1]);
            menus[2] = new JButton("<html><center>사과유자차<br>3,000원</center></html>", TeaImg[2]);
            menus[3] = new JButton("<html><center>얼그레이<br>2,700원</center></html>", TeaImg[3]);
            menus[4] = new JButton("<html><center>페퍼민트<br>2,700원</center></html>", TeaImg[4]);
            menus[5] = new JButton("<html><center>유자차<br>2,500원</center></html>", TeaImg[5]);
            menus[6] = new JButton("<html><center>자몽차<br>2,500원</center></html>", TeaImg[6]);
            menus[7] = new JButton("<html><center>캐모마일<br>2,700원</center></html>", TeaImg[7]);

         }

         else if (ca.equals("음료(BEV)")) {
            menus[0] = new JButton("<html><center>고구마라떼<br>3,200원</center></html>", BevImg[0]);
            menus[1] = new JButton("<html><center>곡물라뗴<br>3,200원</center></html>", BevImg[1]);
            menus[2] = new JButton("<html><center>녹차라떼<br>3,200원</center></html>", BevImg[2]);
            menus[3] = new JButton("<html><center>딸기라떼<br>3,900원</center></html>", BevImg[3]);
            menus[4] = new JButton("<html><center>밀크티라떼<br>3,700원</center></html>", BevImg[4]);
            menus[5] = new JButton("<html><center>아이스초코<br>3,700원</center></html>", BevImg[5]);
            menus[6] = new JButton("<html><center>토피넛라떼<br>3,700원</center></html>", BevImg[6]);
            menus[7] = new JButton("<html><center>흑당버블티<br>3,900원</center></html>", BevImg[7]);

         }

         for (int i = 0; i < menus.length; i++) {
            menus[i].addActionListener(new MenuActionListener());
            menus[i].setPreferredSize(new Dimension(300, 300)); // 원하는 크기로 변경

            customizeButton(menus[i]);

            gbc.gridx = i % 4;
            gbc.gridy = i / 4;
            gbc.fill = GridBagConstraints.BOTH;
            add(menus[i], gbc); // 추가: 패널에 버튼 추가
         }

         this.revalidate();
         this.repaint();
      }

      class MenuActionListener implements ActionListener {
         public void actionPerformed(ActionEvent e) {
            clip.start();
            clip.setFramePosition(0);

            JButton clickedButton = (JButton) e.getSource();
            String menuInfo = clickedButton.getText(); // 버튼의 HTML 콘텐츠를 가져옵니다.

            // HTML 콘텐츠에서 메뉴 이름과 가격을 추출합니다.
            String[] parts = menuInfo.split("<br>");
            String menuName = parts[0].replaceAll("<html><center>", ""); // 메뉴 이름 추출
            String priceString = parts[1].replaceAll("</center></html>", ""); // 가격을 문자열로 추출
            int price = Integer.parseInt(priceString.replaceAll("[^0-9]", "")); // 가격을 정수로 변환

            kiosk.orderPanel.addList(category.cate, menuName, price);

            System.out.println("카테고리: " + category.cate);
            System.out.println("메뉴 이름: " + menuName);
            System.out.println("가격: " + price);
            System.out.println("총 가격: " + kiosk.totalPrice);

         }

      }
   }

   // 데이터 클래스 정의
   public class MenuData {
      private String category;
      private String menu;
      private int price;
      private int moreNum;

      public MenuData(String category, String menu, int price, int moreNum) {
         this.category = category;
         this.menu = menu;
         this.price = price;
         this.moreNum = moreNum;
      }

      public String getCategory() {
         return category;
      }

      public String getMenu() {
         return menu;
      }

      public int getPrice() {
         return price;
      }

      public void setPrice(int price) {
         this.price = price;
      }

      public int getMoreNum() {
         return moreNum;
      }

      public void setMoreNum(int moreNum) {
         this.moreNum = moreNum;
      }
   }

   class RoundButton extends JButton {

      public RoundButton(Icon icon) {
         super(icon);
         setBorderPainted(false);
         setContentAreaFilled(false);
         setFocusPainted(false);
         setOpaque(false);
      }

      @Override
      protected void paintComponent(Graphics g) {
         if (getModel().isArmed()) {
            g.setColor(Color.white);
         } else {
            g.setColor(getBackground());
         }

         Graphics2D graphics2D = (Graphics2D) g;
         graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

         int diameter = Math.min(getWidth(), getHeight());

         super.paintComponent(g);
      }
   }

   public class OrderPanel extends JPanel {
      private KioskFrame kiosk;
      private PaymentPanel paymentPanel;
      private CategoryPanel category;
      private MenuPanel menu;
      private Clip clip;

      private DefaultListModel<MenuData> selectedItemsListModel;
      private JList<MenuData> selectedItemsList;
      private JLabel lblSelectedItemCount;
      private JButton btnToPayment;
      private ImageIcon mascot = new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/Character/미니언즈.png");
      private JLabel mascotLabel = new JLabel(mascot);

      public void loadAudio(String pathName) {
         try {
            clip = AudioSystem.getClip(); // 비어있는 오디오 클립 만들기
            File audioFile = new File(pathName); // 오디오 파일의 경로명
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile); // 오디오 파일로부터
            clip.open(audioStream); // 재생할 오디오 스트림 열기
         } catch (LineUnavailableException e) {
            e.printStackTrace();
         } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }

      }

      public OrderPanel(KioskFrame kiosk, CategoryPanel category, MenuPanel menu) {
         this.kiosk = kiosk;
         this.paymentPanel = new PaymentPanel(kiosk, this, category);
         this.category = category;
         this.menu = menu;

         setLayout(null); // 배치관리자 해제

         // 선택한 상품 목록 리스트
         selectedItemsListModel = new DefaultListModel<>();
         selectedItemsList = new JList<>(selectedItemsListModel);
         selectedItemsList.setCellRenderer(new CustomCellRenderer()); // 셀 렌더러 설정

         JScrollPane scrollPane = new JScrollPane(selectedItemsList);
         scrollPane.setBounds(10, 10, 330, 200);
         //selectedItemsList.setLayout(new GridLayout(0, 6));

         TitledBorder titledBorder = BorderFactory.createTitledBorder("<선택 상품 목록>");
         titledBorder.setTitleJustification(TitledBorder.LEFT);
         titledBorder.setTitleColor(Color.DARK_GRAY);
         titledBorder.setTitleFont(new Font("Malgun Gothic", Font.BOLD, 13));
         scrollPane.setBorder(titledBorder);

         add(scrollPane);

         mascotLabel.setBounds(365, 10, 100, 100);
         add(mascotLabel);

         // <선택상품목록> 라벨 생성
         lblSelectedItemCount = new JLabel(
               "<html><center>선택한 상품 개수 <br>" + kiosk.selectAllCnt + "개</center></html>");
         lblSelectedItemCount.setBounds(365, 115, 100, 30);
         add(lblSelectedItemCount);

         // <결제하기> 버튼의 위치 조정
         btnToPayment = new JButton(
               "<html><div style='text-align: right;'>" + kiosk.totalPrice + "원<br>결제하기</div></html>");
         btnToPayment.setBounds(350, 150, 120, 50);
         btnToPayment.addActionListener(new OrderActionListener());
         add(btnToPayment);

         loadAudio("./audio/selection.wav");
      }

      // 셀 렌더러 클래스
      private class CustomCellRenderer extends JPanel implements ListCellRenderer<MenuData> {
         private JLabel menuLabel;
         private JLabel priceLabel;
         private JLabel categoryLabel;
         private JLabel moreNumLabel; // 한 메뉴의 개수
         private JButton removeButton;
         private JButton plusButton;
         private JButton minusButton;
         private JLabel removeLabel;
         private JLabel plusLabel;
         private JLabel minusLabel;
         private String plus = "C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/ButtonImages/plus.png";
         private String minus = "C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/ButtonImages/minus.png";
         private String remove = "C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/ButtonImages/remove.png";

         public CustomCellRenderer() {
            JPanel cellPanel = new JPanel();
            cellPanel.setLayout(new GridLayout(1, 7));
            
            setOpaque(true);

            ImageIcon plusImg = new ImageIcon(plus);
            ImageIcon minusImg = new ImageIcon(minus);
            ImageIcon removeImg = new ImageIcon(remove);

            plusLabel = new JLabel(plusImg);
            minusLabel = new JLabel(minusImg);
            removeLabel = new JLabel(removeImg);

            // 각 라벨 및 버튼 초기화
            categoryLabel = new JLabel();
            menuLabel = new JLabel();
            moreNumLabel = new JLabel();
            priceLabel = new JLabel();
            
            RoundButton plusButton = new RoundButton(plusImg);
            RoundButton minusButton = new RoundButton(minusImg);
            RoundButton removeButton = new RoundButton(removeImg);

            // 버튼에 리스너 추가
            plusButton.addActionListener(new PlusButtonListener());
            minusButton.addActionListener(new MinusButtonListener());
            removeButton.addActionListener(new RemoveButtonListener());

            add(removeButton);
            add(categoryLabel);
            add(menuLabel);
            add(minusButton);
            add(moreNumLabel);
            add(plusButton);
            add(priceLabel);

            setBackground(Color.WHITE);
            categoryLabel.setForeground(Color.BLACK);
            categoryLabel.setFont(new Font("Hangeulche", Font.BOLD, 9));
            menuLabel.setForeground(Color.BLACK);
            menuLabel.setFont(new Font("Hangeulche", Font.PLAIN, 9));
            priceLabel.setForeground(Color.BLACK);
            priceLabel.setFont(new Font("Hangeulche", Font.BOLD, 9));

            categoryLabel.setFocusable(false);
            menuLabel.setFocusable(false);
            moreNumLabel.setFocusable(false);
            priceLabel.setFocusable(false);
            removeLabel.setFocusable(true);
            plusLabel.setFocusable(true);
            minusLabel.setFocusable(true);
            
            loadAudio("./audio/selection.wav");
         }

         public Component getListCellRendererComponent(JList<? extends MenuData> list, MenuData value, int index,
               boolean isSelected, boolean cellHasFocus) {
            // 라벨들의 텍스트 설정
            categoryLabel.setText(value.getCategory());
            menuLabel.setText(value.getMenu());
            priceLabel.setText(value.getPrice() + "원");
            moreNumLabel.setText(value.getMoreNum() + "개");

            return this;
         }

         private final class PlusButtonListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
               clip.setFramePosition(0);
               clip.start();

               System.out.println("plus button clicked");

               int selectedIndex = selectedItemsList.getSelectedIndex();
               if (selectedIndex != -1) {
                  int index = getMenuDataIndex(
                        selectedItemsListModel.getElementAt(selectedIndex).getCategory(), 
                        selectedItemsListModel.getElementAt(selectedIndex).getMenu());
                  if (index != -1) {
                     MenuData selectedMenuData = selectedItemsListModel.getElementAt(index);
                     int moreNum = selectedMenuData.getMoreNum() + 1;
                     selectedMenuData.setMoreNum(moreNum);

                     // 새로운 수량에 따라 가격 업데이트
                     int pricePerItem = selectedMenuData.getPrice(); // 가격이 이미 정수로 저장되어 있다고 가정
                     int totalPrice = pricePerItem * moreNum;
                     selectedMenuData.setPrice(totalPrice);

                     // 총 가격 업데이트 및 리스트 다시 그리기
                     updateTotalPrice(1, pricePerItem);
                     selectedItemsList.revalidate();
                     selectedItemsList.repaint();
                  }
               }
            }
         }

         private final class MinusButtonListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
               clip.setFramePosition(0);
               clip.start();

               System.out.println("Minus button clicked");

               int selectedIndex = selectedItemsList.getSelectedIndex();

               if (selectedIndex != -1) {
                  MenuData selectedMenu = selectedItemsListModel.getElementAt(selectedIndex);
                  int currentMoreNum = selectedMenu.getMoreNum();

                  if (currentMoreNum > 1) {
                     // 현재 개수가 1보다 크다면 감소
                     selectedMenu.setMoreNum(currentMoreNum - 1);

                     // 새로운 수량에 따라 가격 업데이트
                     int pricePerItem = selectedMenu.getPrice(); // 가격이 이미 정수로 저장되어 있다고 가정
                     int totalPrice = pricePerItem * (currentMoreNum - 1);
                     selectedMenu.setPrice(totalPrice);

                     // 가격 및 개수 라벨 업데이트
                     moreNumLabel.setText(selectedMenu.getMoreNum() + "개");
                     priceLabel.setText(selectedMenu.getPrice() + "원");

                  } else {
                     // 현재 개수가 1이라면 아이템 삭제
                     selectedItemsListModel.removeElementAt(selectedIndex);
                     // 삭제된 경우 총 가격을 감소시키도록 메서드 호출
                     updateTotalPrice(0, selectedMenu.getPrice());
                  }

                  // 리스트 업데이트
                  selectedItemsList.revalidate();
                  selectedItemsList.repaint();
               }
            }
         }

         private final class RemoveButtonListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
               clip.setFramePosition(0);
               clip.start();

               System.out.println("Remove button clicked");

               int selectedIndex = selectedItemsList.getSelectedIndex();

               if (selectedIndex != -1) { // 유효한 선택이 있는 경우
                  MenuData removedMenu = selectedItemsListModel.getElementAt(selectedIndex);

                  // 화면에서 컴포넌트 제거
                  Component[] components = selectedItemsList.getComponents();
                  for (Component component : components) {
                     if (component instanceof JPanel) {
                        JPanel cellPanel = (JPanel) component;
                        if (cellPanel.getComponentCount() > 0) {
                           Component[] subComponents = cellPanel.getComponents();
                           if (subComponents[0] instanceof JLabel) {
                              JLabel categoryLabel = (JLabel) subComponents[0];
                              if (removedMenu.getMenu().equals(categoryLabel.getText())) {
                                 selectedItemsList.remove(cellPanel);
                                 break;
                              }
                           }
                        }
                     }
                  }

                  // selectedItemsListModel에서 아이템 삭제
                  selectedItemsListModel.remove(selectedIndex);

                  // 선택한 메뉴의 수량만큼 kiosk.selectAllCnt와 kiosk.selectMenuCnt 감소
                  kiosk.selectAllCnt -= removedMenu.getMoreNum();
                  kiosk.selectMenuCnt--;

                  // 총 가격에서 선택한 메뉴의 가격과 수량에 따른 금액 감소
                  updateTotalPrice(0, removedMenu.getPrice());

                  // kiosk.selectedMenu 배열에서도 해당 메뉴 제거
                  kiosk.selectedMenu[kiosk.selectMenuCnt + 1] = null;

                  // 화면 갱신
                  selectedItemsList.revalidate();
                  selectedItemsList.repaint();
               }
            }
         }

      }

      public void addList(String category, String menu, int price) {
         kiosk.selectAllCnt++;
         kiosk.selectMenuCnt++;

         int existingIndex = getMenuDataIndex(category, menu);

         if (existingIndex != -1) {
            MenuData existingMenuData = selectedItemsListModel.getElementAt(existingIndex);
            int updatedMoreNum = existingMenuData.getMoreNum() + 1;
            existingMenuData.setMoreNum(updatedMoreNum);

            int updatedPrice = existingMenuData.getPrice() + price;
            existingMenuData.setPrice(updatedPrice);

            // 전체 화면 업데이트
            updateTotalPrice(1, price);
            updateAll(kiosk.totalPrice, kiosk.selectAllCnt);

            // 모델이 변경되었으므로 화면에 반영
            selectedItemsList.revalidate();
            selectedItemsList.repaint();
         } 
         
         else {
            MenuData menuData = new MenuData(category, menu, price, 1);
            selectedItemsListModel.addElement(menuData);

            updateTotalPrice(1, price);
            updateAll(kiosk.totalPrice, kiosk.selectAllCnt);

            // 모델이 변경되었으므로 화면에 반영
            selectedItemsList.revalidate();
            selectedItemsList.repaint();
         }

         if (category.equals("커피(ICE)")) {
            kiosk.totalTime += 2;
         }

         else if (category.equals("커피(HOT)")) {
            kiosk.totalTime += 3;
         }

         else if (category.equals("차(TEA)")) {
            kiosk.totalTime += 3;
         }

         else {
            kiosk.totalTime += 4;
         }
      }

      public int getMenuDataIndex(String targetCategory, String targetMenu) {
         // 해당 음료의 menuData index찾기
         int index = -1; // 초기값으로 -1 설정

         for (int i = 0; i < selectedItemsListModel.getSize(); i++) {
            MenuData menuData = selectedItemsListModel.getElementAt(i);
            String menu = menuData.getMenu();
            String category = menuData.getCategory();

            if (targetMenu.equals(menu) && targetCategory.equals(category)) {
               // 원하는 메뉴를 찾았을 때 해당 인덱스를 저장하고 반복문 종료
               index = i;
               break;
            }
         }
         
         return index;
      }

      public void clearList() {
         kiosk.selectAllCnt = 0;
         kiosk.selectMenuCnt = 0;
         kiosk.selectedMenu = new String[100];
         selectedItemsListModel.clear();
         category.cate = "커피(ICE)";
         menu.changeMenu(category.cate);
         category.displayRandomQuote();

         updateAll(0, 0);
      }

      public void updateAll(int price, int selectAllCnt) {
         String buttonText = ("<html><div style='text-align: right;'>" + price + "원<br>결제하기</div></html>");
         String labelText = ("<html><center>선택한 상품 개수 <br>" + selectAllCnt + "개</center></html>");
         btnToPayment.setText(buttonText);
         lblSelectedItemCount.setText(labelText);
      }

      public void updateTotalPrice(int plusOrminus, int price) {
         // plusOrminus = 1이면 더하기 0이면 빼기
         // 합산 금액 업데이트
         if (plusOrminus == 1) {
            kiosk.totalPrice += price;
            // kiosk.selectAllCnt++;
            // updateAll(kiosk.totalPrice, kiosk.selectAllCnt);
         } else {
            kiosk.totalPrice -= price;
            // kiosk.selectAllCnt--;
            // kiosk.selectMenuCnt--;
            // updateAll(kiosk.totalPrice, kiosk.selectAllCnt);
         }
      }

      public void moveToPayment() {
         // 기존 패널들의 가시성을 숨김
         kiosk.categoryPanel.setVisible(false);
         kiosk.menuPanel.setVisible(false);
         kiosk.orderPanel.setVisible(false);

         // 결제 패널을 추가하고 크기 및 위치 설정
         kiosk.getContentPane().add(paymentPanel);
         paymentPanel.setBounds(0, 0, 500, 700);

         // 화면 갱신
         kiosk.revalidate();
      }

      class OrderActionListener implements ActionListener {
         public void actionPerformed(ActionEvent e) {
            // ToOrder 버튼이 눌렸을 때 수행할 동작
            clip.setFramePosition(0);
            clip.start();

            JButton clickedButton = (JButton) e.getSource();
            String ifClicked = clickedButton.getText(); // 버튼의 HTML 콘텐츠를 가져옵니다.

            String[] parts = ifClicked.split("<br>");
            String menuName = parts[0].replaceAll("<html><div style='text-align: right;'>", ""); // 메뉴 이름 추출
            String priceString = parts[1].replaceAll("</div></html>", ""); // 가격을 문자열로 추출

            // 숫자만 남기고 나머지는 제거
            priceString = priceString.replaceAll("[^0-9]", "");

            // 빈 문자열이라면 0으로 설정하거나 적절한 디폴트 값을 설정
            int price = priceString.isEmpty() ? 0 : Integer.parseInt(priceString);

            kiosk.totalPrice += price;

            if (ifClicked.contains("결제하기")) {
               if (kiosk.totalPrice == 0) {
                  JLabel label = new JLabel("<html><center>결제할 상품이 없습니다.</center></html>", javax.swing.SwingConstants.CENTER);
                  JOptionPane.showMessageDialog(null, label, "결제 실패", JOptionPane.INFORMATION_MESSAGE);
               }

               else {
                  moveToPayment();
               }
            }
         }
      }
   }

   public class PaymentPanel extends JPanel {
      private JLabel lblPaymentMethod;
      private JLabel lblCoupon;
      private JLabel lblCard;
      private JButton btnJavaCoffeeCoupon;
      private JButton btnPhysicalCard;
      private JButton btnSamsungPay;
      private JButton btnReturnToMenu;
      private ImageIcon card = new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/ButtonImages/신용카드.png");
      private ImageIcon samsung = new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/ButtonImages/삼성페이.png");
      private ImageIcon coupon = new ImageIcon("C:/강지후/객체지향언어2/과제/src/JavaCoffee/Images/ButtonImages/쿠폰.png");
      private Clip clip;

      private KioskFrame kiosk;
      private OrderPanel order;
      private CategoryPanel category;

      public void loadAudio(String pathName) {
         try {
            clip = AudioSystem.getClip(); // 비어있는 오디오 클립 만들기
            File audioFile = new File(pathName); // 오디오 파일의 경로명
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile); // 오디오 파일로부터
            clip.open(audioStream); // 재생할 오디오 스트림 열기
         } catch (LineUnavailableException e) {
            e.printStackTrace();
         } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }

      }

      public PaymentPanel(KioskFrame kiosk, OrderPanel order, CategoryPanel category) {
         this.kiosk = kiosk;
         this.order = order;
         this.category = category;

         setLayout(null); // 배치관리자 해제
         setBackground(Color.white);

         // "결제수단 선택" 라벨
         lblPaymentMethod = new JLabel("결제수단 선택");
         lblPaymentMethod.setFont(new Font("Hangeulche", Font.BOLD, 25));
         lblPaymentMethod.setBounds(15, 10, 300, 70);

         add(lblPaymentMethod);

         // "<쿠폰>" 라벨
         lblCoupon = new JLabel("<쿠폰>");
         lblCoupon.setFont(new Font("Hangeulche", Font.BOLD, 25));
         lblCoupon.setBounds(10, 70, 200, 30);
         
         add(lblCoupon);

         // "자바커피 쿠폰" 버튼
         btnJavaCoffeeCoupon = new JButton("<html><br><center>자바커피 쿠폰</center></html>", coupon);
         btnJavaCoffeeCoupon.setFont(new Font("Hangeulche", Font.BOLD, 25));
         btnJavaCoffeeCoupon.setBounds(10, 110, 200, 115);
         btnJavaCoffeeCoupon.setBackground(Color.white);
         
         btnJavaCoffeeCoupon.addActionListener(new PaymentActionListener());
         add(btnJavaCoffeeCoupon);

         // "<카드>" 라벨
         lblCard = new JLabel("<카드>");
         lblCard.setFont(new Font("Hangeulche", Font.BOLD, 25));
         lblCard.setBounds(10, 250, 200, 30);

         add(lblCard);

         // "실물카드" 버튼
         btnPhysicalCard = new JButton("<html><br><center>실물카드</center></html>", card);
         btnPhysicalCard.setFont(new Font("Hangeulche", Font.BOLD, 25));
         btnPhysicalCard.setBounds(10, 290, 200, 138);
         btnPhysicalCard.setBackground(Color.white);
         
         btnPhysicalCard.addActionListener(new PaymentActionListener());
         add(btnPhysicalCard);

         // "삼성페이" 버튼
         btnSamsungPay = new JButton("<html><br><center>삼성페이</center></html>", samsung);
         btnSamsungPay.setFont(new Font("Hangeulche", Font.BOLD, 25));
         btnSamsungPay.setBounds(230, 290, 138, 138);
         btnSamsungPay.setBackground(Color.white);

         btnSamsungPay.addActionListener(new PaymentActionListener());
         add(btnSamsungPay);

         // "메뉴 선택 화면으로 돌아가기" 버튼
         btnReturnToMenu = new JButton("메뉴 선택 화면으로 돌아가기");
         btnReturnToMenu.setFont(new Font("Hangeulche", Font.BOLD, 25));
         btnReturnToMenu.setBackground(Color.lightGray);
         btnReturnToMenu.setBounds(10, 470, 410, 50);
         
         btnReturnToMenu.addActionListener(new PaymentActionListener());
         add(btnReturnToMenu);

         loadAudio("./audio/selection.wav");
      }

      public void updateOrderLabel() {
         category.orderCnt.setText("앞선 주문 건수: " + kiosk.orderCount);
         category.duration.setText("앞선 주문 소요 시간: " + kiosk.timeQ.calculateSum() + "분");
      }

      public void processPayment(int type) {
         // 결제 기능
         String message = "";
         int duration = kiosk.timeQ.calculateSum() + kiosk.totalTime;

         if (type == 1) {
            message = "<html>결제가 완료되었습니다.<br>준비시간은 약 " + duration + "분입니다.</html>";
         }

         else if (type == 2) {
            message = "<html>결제가 완료되었습니다. 카드를 제거해주세요.<br>준비시간은 약 " + duration + "분입니다.</html>";
         }

         else {
            message = "<html>결제가 완료되었습니다. 휴대폰을 제거해주세요.<br>준비시간은 약 " + duration + "분입니다.</html>";
         }

         JLabel label = new JLabel(message, javax.swing.SwingConstants.CENTER);
         
         // 다이얼로그 창 표시
         JOptionPane.showMessageDialog(null, label, "결제 완료", JOptionPane.INFORMATION_MESSAGE);

         // 3초 후에 창이 자동으로 닫히도록 설정
         Timer timer = new Timer(5000, e -> {
            JOptionPane.getRootFrame().dispose(); // 다이얼로그 창 닫기
         });
         
         timer.setRepeats(false); // 한 번만 실행
         timer.start();

         // 주문 건수 증가
         kiosk.orderCount++;
         kiosk.innerOrderCount++;
         // 소요시간 증가
         kiosk.timeQ.enqueue(kiosk.totalTime);

         updateOrderLabel();
      }

      public void moveToMenuSelectionScreen() {
         // 기존 패널들의 가시성 설정
         order.clearList();
         kiosk.categoryPanel.setVisible(true);
         kiosk.menuPanel.setVisible(true);
         kiosk.orderPanel.setVisible(true);

         // 결제 패널 제거
         kiosk.getContentPane().remove(kiosk.orderPanel.paymentPanel);

         // 화면 갱신
         kiosk.revalidate();
         kiosk.repaint();
      }

      public void backToMenuSelectionScreen() {
         kiosk.categoryPanel.setVisible(true);
         kiosk.menuPanel.setVisible(true);
         kiosk.orderPanel.setVisible(true);

         // 결제 패널 제거
         kiosk.getContentPane().remove(kiosk.orderPanel.paymentPanel);

         // 화면 갱신
         kiosk.revalidate();
         kiosk.repaint();
      }

      class PaymentActionListener implements ActionListener {
         public void actionPerformed(ActionEvent e) { // PaymentPanel에서 수행할 작업
            clip.setFramePosition(0);
            clip.start();

            if (e.getActionCommand().contains("자바커피 쿠폰")) {
               // "자바커피 쿠폰" 버튼 클릭 시 수행할 작업
               processPayment(1);
               kiosk.init();
               moveToMenuSelectionScreen();
            }

            else if (e.getActionCommand().contains("실물카드")) {
               // "실물카드" 버튼 클릭 시 수행할 작업
               processPayment(2);
               kiosk.init();
               moveToMenuSelectionScreen();
            }

            else if (e.getActionCommand().contains("삼성페이")) {
               // "삼성페이" 버튼 클릭 시 수행할 작업
               processPayment(3);
               kiosk.init();
               moveToMenuSelectionScreen();
            }

            else if (e.getSource() == btnReturnToMenu) {
               // "메뉴 선택 화면으로 돌아가기" 버튼 클릭 시 수행할 작업
               backToMenuSelectionScreen();
            }
         }
      }
   }

   public class PosPanel extends JPanel {
      private JLabel label;
      private JLabel subLabel;
      private JButton btn1;
      private JButton btn2;
      private JButton btn3;
      private POSFrame pos;
      private KioskFrame kiosk;
      private PaymentPanel payment;
      private OrderPanel order;
      private CategoryPanel category;
      private MenuPanel menu;
      private Clip clip;

      public void loadAudio(String pathName) {
         try {
            clip = AudioSystem.getClip(); // 비어있는 오디오 클립 만들기
            File audioFile = new File(pathName); // 오디오 파일의 경로명
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile); // 오디오 파일로부터
            clip.open(audioStream); // 재생할 오디오 스트림 열기
         } catch (LineUnavailableException e) {
            e.printStackTrace();
         } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }

      }
      
      public PosPanel(POSFrame pos, KioskFrame kiosk) {
         this.kiosk = kiosk;
         this.pos = pos;
         this.payment = new PaymentPanel(kiosk, order, category);
         this.category = category;

         setLayout(null);

         // 버튼 생성
         label = new JLabel("JAVA");
         subLabel = new JLabel("COFFEE");
         btn1 = new JButton("매장 준비 완료");
         btn2 = new JButton("배달 접수(준비)");
         btn3 = new JButton("배달 준비 완료");

         label.setBounds(25, 10, 300, 50);
         subLabel.setBounds(17, 50, 300, 30);
         btn1.setBounds(40, 120, 120, 50);
         btn2.setBounds(180, 120, 120, 50);
         btn3.setBounds(320, 120, 120, 50);

         btn1.setBackground(Color.lightGray);
         btn2.setBackground(Color.lightGray);
         btn3.setBackground(Color.LIGHT_GRAY);

         label.setFont(new Font("Arial", Font.BOLD, 40));
         subLabel.setFont(new Font("Arial", Font.PLAIN, 30));

         add(label);
         add(subLabel);
         add(btn1);
         add(btn2);
         add(btn3);
         
         loadAudio("./audio/selection.wav");

         class PosActionListener implements ActionListener {
            private KioskFrame kiosk;
            private PaymentPanel payment;
            private CategoryPanel category;

            public PosActionListener(KioskFrame kiosk, PaymentPanel payment, CategoryPanel category) {
               this.kiosk = kiosk;
               this.payment = payment;
               this.category = category;
            }

            public void actionPerformed(ActionEvent e) {
               String message = "";
               clip.setFramePosition(0);
               clip.start();
               
               if (e.getSource() == btn1) {
                  // "매장 준비 완료"
                  if (kiosk.innerOrderCount != 0) {
                     kiosk.orderCount--;
                     kiosk.innerOrderCount--;
                     kiosk.timeQ.dequeue();
                     
                     System.out.println("매장 주문이 준비되었습니다.");
                     
                     message = "<html>매장 주문이 준비되었습니다.<br>남은 주문 건수는 " + kiosk.innerOrderCount + "건입니다.";

                     JLabel label = new JLabel(message, javax.swing.SwingConstants.CENTER);

                     // 다이얼로그 창 표시
                     JOptionPane.showMessageDialog(null, label, "주문 준비 완료",JOptionPane.INFORMATION_MESSAGE);
      
                     kiosk.getCategoryPanel().orderCnt.setText("앞선 주문 건수: " + kiosk.orderCount);
                     kiosk.getCategoryPanel().duration.setText("앞선 주문 소요 시간: " + kiosk.timeQ.calculateSum() + "분");
                  }

                  else {
                     System.out.println("주문이 없습니다.");
                     //String message = "";
                     message = "매장 주문이 없습니다.";

                     JLabel label = new JLabel(message, javax.swing.SwingConstants.CENTER);

                     // 다이얼로그 창 표시
                     JOptionPane.showMessageDialog(null, label, "전산 오류",JOptionPane.INFORMATION_MESSAGE);
                  }

               }

               else if (e.getSource() == btn2) {
                  // "배달 접수(준비)"
                  kiosk.orderCount++;
                  kiosk.deliveryOrderCount++;
                  kiosk.timeQ.enqueue(delivery);
                  
                  kiosk.getCategoryPanel().orderCnt.setText("앞선 주문 건수: " + kiosk.orderCount);
                  kiosk.getCategoryPanel().duration.setText("앞선 주문 소요 시간: " + kiosk.timeQ.calculateSum() + "분");
               }

               else if (e.getSource() == btn3) {
                  // "배달 준비 완료"
                  if (kiosk.deliveryOrderCount != 0) {
                     kiosk.orderCount--;
                     kiosk.deliveryOrderCount--;
                     kiosk.timeQ.removeElementAtIndex(kiosk.timeQ.indexOf(delivery));
                     
                     message = "<html>배달 주문이 준비되었습니다.<br>남은 주문 건수는 " + kiosk.deliveryOrderCount + "건입니다.";

                     JLabel label = new JLabel(message, javax.swing.SwingConstants.CENTER);

                     // 다이얼로그 창 표시
                     JOptionPane.showMessageDialog(null, label, "주문 준비 완료",JOptionPane.INFORMATION_MESSAGE);
                     
                     kiosk.getCategoryPanel().orderCnt.setText("앞선 주문 건수: " + kiosk.orderCount);
                     kiosk.getCategoryPanel().duration.setText("앞선 주문 소요 시간: " + kiosk.timeQ.calculateSum() + "분");
                  }

                  else {
                     System.out.println("주문이 없습니다.");
                     //String message = "";
                     message = "배달 주문이 없습니다.";

                     JLabel label = new JLabel(message, javax.swing.SwingConstants.CENTER);
                     
                     // 다이얼로그 창 표시
                     JOptionPane.showMessageDialog(null, label, "전산 오류",JOptionPane.INFORMATION_MESSAGE);

                  }
               }
            }
         }

         btn1.addActionListener(new PosActionListener(kiosk, payment, category));
         btn2.addActionListener(new PosActionListener(kiosk, payment, category));
         btn3.addActionListener(new PosActionListener(kiosk, payment, category));
      }

   }

   public class KioskFrame extends JFrame {
      private CategoryPanel categoryPanel;
      private MenuPanel menuPanel;
      private OrderPanel orderPanel;
      private PaymentPanel paymentPanel;
      private PosPanel posPanel;
      private POSFrame pos;
      private StartPanel startPanel;
      public totalTimeQueue timeQ = new totalTimeQueue(); // 앞선 주문의 소요시간 저장하는 큐(선입선출)

      String selectedMenu[]; // (한 주문의)선택된 메뉴
      // ( 아아2개,라테1개 -> selectMenuCnt=2, selectAllCnt=3 )
      public int selectAllCnt; // 주문할 총 음료 개수
      public int selectMenuCnt; // 주문할 음료 종류의 수
      public int orderCount; // (모든 손님의 수) 주문 건수
      public int innerOrderCount; // 매장 주문 건수
      public int deliveryOrderCount; // 배달 주문 건수
      public int totalPrice; // 총 금액
      public int totalTime; // 총 소요 시간

      public void setPosPanel(PosPanel posPanel) {
         this.posPanel = posPanel;
      }

      public KioskFrame(PosPanel posPanel) {
         this.posPanel = posPanel;

         selectedMenu = new String[100];
         selectAllCnt = 0;
         innerOrderCount = 0;
         deliveryOrderCount = 0;
         orderCount = innerOrderCount + deliveryOrderCount;
         totalPrice = 0;

         // 프레임 설정
         setTitle("Java Coffee 키오스크");
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setLayout(null);

         // 각 패널 초기화
         startPanel = new StartPanel(this);
         startPanel.setPreferredSize(new Dimension(500, 800));
         startPanel.setBounds(0, 0, 500, 700);

         add(startPanel);

         // 추가 설정
         getContentPane().setBackground(Color.white);
         setSize(500, 700);
         setVisible(true);
         setLocation(200, 50);
      }

      public void checkIfClicked(int ifClicked) {
         if (ifClicked == 1) {
            startPanel.setVisible(false);

            categoryPanel = new CategoryPanel(this);
            menuPanel = new MenuPanel(this, categoryPanel);
            orderPanel = new OrderPanel(this, categoryPanel, menuPanel);

            categoryPanel.setPreferredSize(new Dimension(500, 300));
            menuPanel.setPreferredSize(new Dimension(500, 400));
            orderPanel.setPreferredSize(new Dimension(500, 200));

            categoryPanel.setBounds(0, 0, 500, 150);
            menuPanel.setBounds(0, 150, 500, 300);
            orderPanel.setBounds(0, 440, 500, 300);

            categoryPanel.setBackground(Color.white);
            menuPanel.setBackground(Color.white);
            orderPanel.setBackground(Color.white);

            add(categoryPanel);
            add(menuPanel);
            add(orderPanel);
         }
      }

      public CategoryPanel getCategoryPanel() {
         return categoryPanel;
      }

      public MenuPanel getMenuPanel() {
         return menuPanel;
      }

      public void init() {
         selectedMenu = new String[100];
         selectAllCnt = 0;
         totalPrice = 0;
         totalTime = 0;
      }
   }

   public class POSFrame extends JFrame {
      private PosPanel posPanel;
      private KioskFrame kiosk;
      private OrderPanel order;
      private CategoryPanel categoryPanel;
      String name;// 메뉴명
      String category; // 카테고리
      double price; // 가격

      public POSFrame(KioskFrame kiosk) {
         // 프레임 설정
         setTitle("POS");
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

         // 패널 초기화
         posPanel = new PosPanel(this, kiosk);

         // 패널을 프레임에 추가
         add(posPanel);

         // 추가 설정
         // getContentPane().setBackground(Color.white);
         posPanel.setBackground(Color.white);
         setSize(500, 300);
         setVisible(true);
         setLocation(800, 150);
      }
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(CoffeeKiosk::new);
   }
}