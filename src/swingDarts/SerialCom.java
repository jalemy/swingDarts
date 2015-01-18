package swingDarts;

import gnu.io.*;

import java.io.*;
import java.math.BigDecimal;

public class SerialCom {
	// 入出力ストリーム
	InputStream input;
	OutputStream output;
	
	// ポートの状態通知用
	String portStatus;
	
	// シリアル通信を利用するためのクラス
	SerialPort port;
	CommPortIdentifier comID;
	
	// ボーレート, デジタルデータを1秒間に何回変復調できるかを示す値
	int baudRate;
	// openなどで指定するポート名
	String comName;
	
	// 投擲データ保管用クラス
	DartsData dartsData = new DartsData();
	
	// Sift計算用クラス
	SiftParameter sift = new SiftParameter();
		
	boolean SerialOpen(String comName, int baudrate) {
		try {
			// 引数comNameの名前で新たにCOMポートを取得
			comID = CommPortIdentifier.getPortIdentifier(comName);
		} catch (NoSuchPortException e) {
			portStatus =  "ポートを取得できませんでした ";
			return false;
		}
		
		// ポートが開いていない場合、ポートを開いて各種設定を行う
		if (comID.isCurrentlyOwned() == false) {
			try {
				// 第1引数はこのポートを使用するアプリケーション名, 第2引数はタイムアウト時間
				// シリアルポートのインスタンスを生成
				port = (SerialPort)comID.open("swingDarts", 2000);
				
				// イベントリスナーを登録
				port.addEventListener(new SerialPortListener());
				
				// 49byte受信したらイベント発生するように設定
				port.enableReceiveThreshold(49);
				
				// イベント監視をONにする
				port.notifyOnDataAvailable(true);
				
				// ボーレート, データビット数, ストップビット数, パリティを設定
				port.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				
				// フロー制御無し
				port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
				
				// 入出力ストリーム設定
				input = port.getInputStream();
				output = port.getOutputStream();
			} catch (Exception e) {
				portStatus = "ポートを開くことに失敗しました";
				return false;
			}
		} else {
			portStatus = "既にポートが開いています";
			return false;
		}
		
		// 正常終了ならば、trueを返す
		portStatus = "ポートを開くことに成功しました";
		return true;
	}
	
	// ポートcloseのメソッド
	boolean SerialClose() {
		if (comID == null) {
			portStatus = "ポートが選択されていません";
			return false;
		}
		// もしポートが開いている場合は……
		if (comID.isCurrentlyOwned()) {
			try {
				port.close();
			} catch (Exception e) {
				portStatus = "ポートを閉じることに失敗しました";
				return false;
			}
		} else {
			portStatus = "ポートは既に閉じています";
			return false;
		}
		
		// 正常終了ならば……
		portStatus = "ポートを閉じることに成功しました";
		
		// test用//////////////////
		try {
			File file = new File("test.csv");
			
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			
			for (int i = 0; i < dartsData.accelX.size(); i++) {
				pw.println(dartsData.accelX.get(i) + "," +
						   dartsData.accelY.get(i) + "," +
						   dartsData.accelZ.get(i) + "," +
						   dartsData.gyroX.get(i) + "," +
						   dartsData.gyroY.get(i) + "," +
						   dartsData.gyroZ.get(i));
			}
			pw.close();
			System.out.println("file output succeeded");
		} catch (IOException e) {
			// 特になし
			System.out.println("file error");
		}
		
		return true;
	}
	
	class SerialPortListener implements SerialPortEventListener {
		// バッファー
		byte[] readBuffer = new byte[49];
		
		int cnt = 0;
		// 受信イベント発生時に呼び出されるメソッド
		public void serialEvent(SerialPortEvent event) {
			if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
				try {
					input.read(readBuffer);
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				
				int ax[] = new int[4];
				int ay[] = new int[4];
				int az[] = new int[4];
				int gx[] = new int[4];
				int gy[] = new int[4];
				int gz[] = new int[4];
				
				// バッファーに受け取ったものをビットシフトしてint型として格納
				for (int i = 0; i < 4; i++) {
					ax[i] = (readBuffer[i * 12 + 1] << 8)  + readBuffer[i * 12 + 2];
					ay[i] = (readBuffer[i * 12 + 3] << 8)  + readBuffer[i * 12 + 4];
					az[i] = (readBuffer[i * 12 + 5] << 8)  + readBuffer[i * 12 + 6];
					gx[i] = (readBuffer[i * 12 + 7] << 8)  + readBuffer[i * 12 + 8];
					gy[i] = (readBuffer[i * 12 + 9] << 8)  + readBuffer[i * 12 + 10];
					gz[i] = (readBuffer[i * 12 + 11] << 8) + readBuffer[i * 12 + 12];
				}
				
				// dpsに変換
				for (int i = 0; i < 4; i++) {
					ax[i] = ax[i] * 2000 / 32767;
					ay[i] = ay[i] * 2000 / 32767;
					az[i] = az[i] * 2000 / 32767;
					gx[i] = gx[i] * 2000 / 32767;
					gy[i] = gy[i] * 2000 / 32767;
					gz[i] = gz[i] * 2000 / 32767;
				}
				
				dartsData.addAx(ax);
				dartsData.addAy(ay);
				dartsData.addAz(az);
				dartsData.addGx(gx);
				dartsData.addGy(gy);
				dartsData.addGz(gz);
				
				// グラフ描画用のデータを4要素ずらす
				dartsData.displaceData();
				// 後ろ4要素を新たなデータに書き換え
				for (int i = 0; i < 4; i++) {
					dartsData.gDataX[296 + i] = gx[i];
					dartsData.gDataZ[296 + i] = gz[i];
				}
				
				/*
				System.out.println("SEQ:" + readBuffer[0]);
				for (int i = 0; i < 4; i++) {
					System.out.println("ax: " + ax[i] + " ay: " + ay[i] + " az: " + az[i]);
					System.out.println("gz: " + gx[i] + " gy: " + gy[i] + " gz: " + gz[i]);
				}
				*/
				// -----
				for (int i = 0; i < 4; i++) {
					sift.t += 0.01;
					sift.ax = ax[i];
					sift.ay = ay[i];
					sift.az = az[i];
					sift.gx = gx[i];
					sift.gy = gy[i];
					sift.gz = gz[i];
					
					sift.calcSift();
				}
			}
		}
	}
}
