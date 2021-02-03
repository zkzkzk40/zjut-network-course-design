import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;

public class Client{
  JFrame f = new JFrame("聊天室客户器端");
  //组件添加-------------------------------------------------------------------------------------------------------

  //启动服务和停止服务
  JButton Logon = new JButton("登陆");//登陆
  JButton Logout = new JButton("注销");//注销

  //消息
  JLabel SendMes = new JLabel("发送消息：");//发送消息标签
  JTextField MesField = new JTextField(70);//发送消息框
  JButton Send = new JButton("发送");//发送按钮

  //文本域的内容
  JTextArea TextContent= new JTextArea();//文本内容
  JScrollPane ScrollT = new JScrollPane(TextContent);//给文本内容添加滚动条

  //是否登录
  String IP;
  Socket socket;
  BufferedWriter bufferedWriter;

  Client(){
    //消息框字体显示
    MesField.setFont(new Font("楷体",Font.BOLD,23));
    Logout.setEnabled(false);
    init();
    //三个按钮事件响应
    Logon.addActionListener(e -> {
      Logon.setEnabled(false);
      Logout.setEnabled(true);
      try {
        IP=Inet4Address.getLocalHost().getHostAddress();
        socket = new Socket(IP, 8000);
        //通过socket获取字符流
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        //获取服务器返回值
        new Thread(() -> {
          try {
            DataInputStream is= new DataInputStream(socket.getInputStream());
            String info = null;
            while((info=is.readUTF())!=null){
              TextContent.append(info);
            }
          } catch (IOException ioException) {
            ioException.printStackTrace();
          }
        }).start();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    });
    Logout.addActionListener(e -> {
      Logon.setEnabled(true);
      Logout.setEnabled(false);
      try {
        sendMessage("已退出");
        socket.shutdownOutput();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }

    });
    Send.addActionListener(e -> {
      String message=MesField.getText();
      sendMessage(message);
      MesField.setText("");
    });
  }

  public void init(){
    //组装启动服务和停止服务
    Box top = Box.createHorizontalBox();
    top.add(Logon); //添加开始服务
    top.add(Box.createHorizontalStrut(10));
    top.add(Logout);  //添加停止服务
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

    //TextContent的设置
    TextContent.setLineWrap(true);//设置文本域的自动换行
    TextContent.setWrapStyleWord(true);//激活换行不换字
    TextContent.setFont(new Font("楷体",Font.BOLD,18));
    TextContent.setEditable(false);
    //default Setting
    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    f.pack();
    f.setBounds(0,0,500, 400);
    f.setVisible(true);
    f.setLocationRelativeTo(null);
  }
  private void  sendMessage(String message){
    try {
      bufferedWriter.write(message+"\n");
      bufferedWriter.flush();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  public static void main(String[] args) {
    new Client();
    new Client();

  }

};
