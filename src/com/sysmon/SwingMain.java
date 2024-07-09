package com.sysmon;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SwingMain extends Application {
	
	private OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	private XYChart.Series<Number, Number> cpuSeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> ramSeries = new XYChart.Series<>();
    private int time = 0;
	
	@SuppressWarnings("deprecation")
	@Override
    public void start(Stage stage) throws Exception {
		Text cpuUsageText = new Text();
        Text ramUsageText = new Text();
        Text cpuCountText = new Text();
        Text systemLoadText = new Text();
        Text freePhysicalMemoryText = new Text();
        Text totalPhysicalMemoryText = new Text();
        Text freeSwapSpaceText = new Text();
        Text totalSwapSpaceText = new Text();
        ProgressBar cpuProgressBar = new ProgressBar(0);
        ProgressBar ramProgressBar = new ProgressBar(0);

        // Add style classes
        cpuUsageText.getStyleClass().add("text-cpu");
        ramUsageText.getStyleClass().add("text-ram");

        // Create line chart
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (seconds)");
        yAxis.setLabel("Usage (%)");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("System Usage Over Time");
        cpuSeries.setName("CPU Usage");
        ramSeries.setName("RAM Usage");
        lineChart.getData().add(cpuSeries);
        lineChart.getData().add(ramSeries);

        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);
        root.getChildren().addAll(
                cpuUsageText, cpuProgressBar,
                ramUsageText, ramProgressBar,lineChart,
                cpuCountText, systemLoadText,
                freePhysicalMemoryText, totalPhysicalMemoryText,
                freeSwapSpaceText, totalSwapSpaceText
            );
        Scene scene = new Scene(root, 800, 750);

        // Upload CSS file
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("System Monitor");
        stage.setResizable(false);
        stage.show();

        // Set timer
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            // CPU Usage
            double cpu = osBean.getSystemCpuLoad() * 100;
            cpuUsageText.setText(String.format("CPU Usage: %.2f%%", cpu));
            cpuProgressBar.setProgress(cpu / 100);
            cpuSeries.getData().add(new XYChart.Data<>(time, cpu));

            // RAM Usage (Estimated)
            long totalMemory = osBean.getTotalPhysicalMemorySize();
            long freeMemory = osBean.getFreePhysicalMemorySize();
            long usedMemory = totalMemory - freeMemory;
            double ramPercentage = (double) usedMemory / totalMemory * 100;
            ramUsageText.setText(String.format("RAM Usage: %.2f%%", ramPercentage));
            ramProgressBar.setProgress(ramPercentage / 100);
            ramSeries.getData().add(new XYChart.Data<>(time, ramPercentage));
            
            int cpuCount = osBean.getAvailableProcessors();
			double systemLoad = osBean.getSystemCpuLoad() * 100;
            long freePhysicalMemorySize = osBean.getFreePhysicalMemorySize();
            long totalPhysicalMemorySize = osBean.getTotalPhysicalMemorySize();
            long freeSwapSpaceSize = osBean.getFreeSwapSpaceSize();
            long totalSwapSpaceSize = osBean.getTotalSwapSpaceSize();
            
            cpuCountText.setText(String.format("CPU Count: %d", cpuCount));
            systemLoadText.setText(String.format("System Load: %.2f%%", systemLoad));
            freePhysicalMemoryText.setText(String.format("Free Physical Memory: %d MB", freePhysicalMemorySize / (1024 * 1024)));
            totalPhysicalMemoryText.setText(String.format("Total Physical Memory: %d MB", totalPhysicalMemorySize / (1024 * 1024)));
            freeSwapSpaceText.setText(String.format("Free Swap Space: %d MB", freeSwapSpaceSize / (1024 * 1024)));
            totalSwapSpaceText.setText(String.format("Total Swap Space: %d MB", totalSwapSpaceSize / (1024 * 1024)));

            // update time
            time++;
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
