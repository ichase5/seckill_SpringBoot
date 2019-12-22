package com.example.seckill2.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.seckill2.domain.SeckillUser;


/*
新建5000个用户，并插入数据库。记录token，进行测试
 */
public class SeckillTestUtil {
	
	private static void createUser(int count) throws Exception{

		//生成用户
		List<SeckillUser> users = new ArrayList<>(count);

		for(int i=0;i<count;i++) {
			SeckillUser user = new SeckillUser();
			user.setId(12000000000L+i);
			user.setLoginCount(1);
			user.setNickname("user"+i);
			user.setRegisterDate(new Date());
			user.setSalt("xxy");//服务端随机加盐
			user.setPassword(MD5Util.password2ServerEncoding("960525", user.getSalt()));
			users.add(user);
		}
		System.out.println("create user completed");


//		//插入秒杀用户数据库,这里比较慢(这里没有使用pstmt的batch方式，因为发现本机最大限制在batch=1000)
//		Connection conn = DBUtil.getConn();
//		String sql = "insert into seckill_user(login_count, nickname, register_date, salt, password, id)values(?,?,?,?,?,?)";
//		PreparedStatement pstmt = conn.prepareStatement(sql);
//		for(int i=0;i<users.size();i++) {
//			SeckillUser user = users.get(i);
//			pstmt.setInt(1, user.getLoginCount());
//			pstmt.setString(2, user.getNickname());
//			pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
//			pstmt.setString(4, user.getSalt());
//			pstmt.setString(5, user.getPassword());
//			pstmt.setLong(6, user.getId());
//			pstmt.execute();
//			System.out.println( "插入了" + (i+1) + " / " + users.size() + "个用户了" );
//		}
//		pstmt.close();
//		conn.close();
//		System.out.println("插入用户数据库完成");


		//登录，生成token。Redis中生成分布式Session
		String urlString = "http://localhost:8080/login/do_login";//登录接口地址
		File file = new File("D:\\apache-jmeter-5.2.1\\tokens.txt");//windows
		//File file = new File("/home/leibo/Downloads/apache-jmeter-5.2.1/tokens.txt");//linux
		if(file.exists()) {
			file.delete();
		}
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		file.createNewFile();
		raf.seek(0);
		for(int i=0;i<users.size();i++) {
			SeckillUser user = users.get(i);
			URL url = new URL(urlString);
			HttpURLConnection co = (HttpURLConnection)url.openConnection();
			co.setRequestMethod("POST");
			co.setDoOutput(true);
			OutputStream out = co.getOutputStream();
			String params = "mobile="+user.getId()+"&password="+MD5Util.clientEncoding("960525");
			out.write(params.getBytes());
			out.flush();
			InputStream inputStream = co.getInputStream();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte buff[] = new byte[1024];
			int len = 0;
			while((len = inputStream.read(buff)) >= 0) {
				bout.write(buff, 0 ,len);
			}
			inputStream.close();
			bout.close();
			String response = new String(bout.toByteArray());
			JSONObject jo = JSON.parseObject(response);
			String token = jo.getString("data");
			System.out.println("create token : " + user.getId());
			
			String row = user.getId()+","+token;
			raf.seek(raf.length());
			raf.write(row.getBytes());
			raf.write("\r\n".getBytes());
			System.out.println("write to file : " + user.getId());
		}
		raf.close();
		
		System.out.println("token与sessin生成完毕");
	}
	
	public static void main(String[] args)throws Exception {
		createUser(5000);
	}
}
