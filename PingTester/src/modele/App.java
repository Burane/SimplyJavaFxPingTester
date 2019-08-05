package modele;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class App {

	private ScheduledExecutorService executor;

	private String ipAddress = "";

	@FXML
	private LineChart<String, Number> pingChart;

	@FXML
	private CategoryAxis xAxis;

	@FXML
	private NumberAxis yAxis;

	private boolean lance = false;

	@FXML
	private TextField ipAdresse;

	@FXML
	public TextArea infosPing;

	@FXML
	private Font x7;

	@FXML
	private Color x8;

	@FXML
	private Font x3;

	@FXML
	private Color x4;

	private Window stage;

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void app() {

		xAxis.setLabel("Temps/s");
		yAxis.setLabel("ping/ms");
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		pingChart.getData().add(series);
		pingChart.setLegendVisible(false);
		pingChart.setCreateSymbols(false);

		final Runnable task = new Runnable() {
			int ping = 0;

			@Override
			public void run() {
				try {
					ping = sendPingRequest();
					infosPing.appendText(date(1) + " " + ipAddress + " a répondu en → " + ping + " ms \n");

					Platform.runLater(() -> {
						series.getData().add(new XYChart.Data<>(date(2), ping));
						if (series.getData().size() > 15)
							series.getData().remove(0);
					});

				} catch (UnknownHostException e) {
					infosPing.appendText(e + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
	}

	public int sendPingRequest() throws UnknownHostException, IOException {
		InetAddress adresse = InetAddress.getByName(ipAddress);
		long temps1 = System.currentTimeMillis();
		if (adresse.isReachable(5000))
			return (int) (System.currentTimeMillis() - temps1);
		else
			return (5000);
	}

	public static String date(int choix) {
		SimpleDateFormat dateFormat = null;
		if (choix == 1)
			dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:s : ");
		else if (choix == 2)
			dateFormat = new SimpleDateFormat("hh:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public void setInfosPing(String infos) {
		infosPing.appendText(infos);
	}

	@FXML
	void onGo(ActionEvent event) {
		if (!lance) {
			setIpAddress(ipAdresse.getText());
			app();
			lance = true;
		} else
			setIpAddress(ipAdresse.getText());
	}

	@FXML
	void onStop(ActionEvent event) {
		executor.shutdownNow();
		lance = false;
		pingChart.getData().clear();
	}

	public TextField getIpAdresse() {
		return ipAdresse;
	}

	public void setIpAdresse(TextField ipAdresse) {
		this.ipAdresse = ipAdresse;
	}

	public TextArea getInfosPing() {
		return infosPing;
	}

	@FXML
	void onQuit(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	void onSave(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("txt files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);

		File file = fileChooser.showSaveDialog(this.stage);

		if (file != null) {
			FileWriter ffw;
			try {
				ffw = new FileWriter(file);
				ffw.write(infosPing.getText());
				ffw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
