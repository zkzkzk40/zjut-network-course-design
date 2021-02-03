import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Server {
  JFrame f = new JFrame("聊天室服务器端");
  //组件添加-------------------------------------------------------------------------------------------------------

  //启动服务和停止服务
  JButton StartSer = new JButton("启动服务");//启动服务
  JButton EndSer = new JButton("停止服务");//停止服务

  //消息
  JLabel SendMes = new JLabel("发送消息：");//发送消息标签
  JTextField MesField = new JTextField(70);//发送消息框
  String Message;//消息内容
  JButton Send = new JButton("发送");//发送按钮

  //文本域的内容
  JTextArea TextContent= new JTextArea();//文本内容
  JScrollPane ScrollT = new JScrollPane(TextContent);//给文本内容添加滚动条
  Map<Socket,Integer> socketMap=new HashMap<>();
  ServerSocket serverSocket;
  String message;
  Socket socket;
  int count=1;
  Server(){
    //消息框字体显示
    MesField.setFont(new Font("楷体",Font.BOLD,23));
    init();
    //三个按钮事件响应
    StartSer.addActionListener(e -> {
      try {
        serverSocket = new ServerSocket(8000);
        while (true) {
          //等待客户端的连接
          socket = serverSocket.accept();
          socketMap.put(socket,socketMap.size()+1);
          int index=count++;
          sentMessageToAll("已上线",index);
          //每当有一个客户端连接进来后，就启动一个单独的线程进行处理
          new Thread(new Runnable() {
            @Override
            public void run() {
              //获取输入流,并且指定统一的编码格式
              BufferedReader bufferedReader = null;
              try {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                //读取一行数据
                //通过while循环不断读取信息
                while ((message = bufferedReader.readLine()) != null) {
                  //输出打印
                  sentMessageToAll(message,index);
                }
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          }).start();
        }
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    });
    EndSer.addActionListener(e -> {});
    Send.addActionListener(e -> {});
  }
  private void sentMessageToAll(String message,int index){
    try {
      for(Map.Entry<Socket,Integer> entry:socketMap.entrySet()){
        DataOutputStream dataOutputStream = new DataOutputStream(entry.getKey().getOutputStream());
        dataOutputStream.writeUTF("用户"+index+":"+message+"\n");//转发消息
        dataOutputStream.flush();
      }
    } catch (IOException ioException) {
    ioException.printStackTrace();
  }
  }
  public void init(){
    //组装启动服务和停止服务
    Box top = Box.createHorizontalBox();
    top.add(StartSer); //添加开始服务
    top.add(Box.createHorizontalStrut(10));
    top.add(EndSer);  //添加停止服务
    f.add(top, BorderLayout.NORTH);//添加top至顶部

    //组装TextContent
    Box Center = Box.createVerticalBox();
    Center.add(Box.createVerticalStrut(5));
    Center.add(TextContent);
    Center.add(Box.createVerticalStrut(5));
    f.add(Center, BorderLayout.CENTER);//添加TextContent至中心

    //组装底部
    Box Bottom = Box.createHorizontalBox();
    Bottom.add(Box.createHorizontalStrut(10));
    Bottom.add(SendMes);//添加发送消息标签
    Bottom.add(Box.createHorizontalStrut(10));
    Bottom.add(MesField);//添加消息框
    Bottom.add(Box.createHorizontalStrut(10));
    Bottom.add(Send);//添加发送按钮
    f.add(Bottom, BorderLayout.SOUTH);//添加Bottom至底部

    //按钮的设置
    //TextContent的设置
    TextContent.setLineWrap(true);//设置文本域的自动换行
    TextContent.setWrapStyleWord(true);//激活换行不换字
    TextContent.setFont(new Font("楷体",Font.BOLD,18));

    //default Setting
    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    f.pack();
    f.setBounds(0,0,500, 400);
    f.setVisible(true);
    f.setLocationRelativeTo(null);
  }
  public static void main(String[] args) {
    new Server();

  }
}