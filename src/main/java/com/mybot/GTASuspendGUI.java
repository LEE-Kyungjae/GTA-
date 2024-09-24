package com.mybot;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class GTASuspendGUI extends JFrame {
    private JButton suspendButton;
    private JButton resumeButton;
    private JButton autoSuspendResumeButton;
    private JLabel statusLabel;
    private int processId = ProcessFinder.findProcessId("GTA5.exe");

    public GTASuspendGUI() {
        setTitle("GTA 일시정지 관리자");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        suspendButton = new JButton("일시정지");
        resumeButton = new JButton("재개");
        autoSuspendResumeButton = new JButton("자동 일시정지 후 재개");
        statusLabel = new JLabel("상태: 대기 중...");

        suspendButton.setBounds(20, 30, 100, 30);
        resumeButton.setBounds(140, 30, 100, 30);
        autoSuspendResumeButton.setBounds(20, 70, 220, 30);
        statusLabel.setBounds(20, 110, 250, 30);

        add(suspendButton);
        add(resumeButton);
        add(autoSuspendResumeButton);
        add(statusLabel);

        // 수동 일시정지
        suspendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ProcessControl.suspendProcess(processId)) {
                    statusLabel.setText("상태: 프로세스 일시정지됨");
                } else {
                    statusLabel.setText("상태: 일시정지 실패");
                }
            }
        });

        // 수동 재개
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ProcessControl.resumeProcess(processId)) {
                    statusLabel.setText("상태: 프로세스 재개됨");
                } else {
                    statusLabel.setText("상태: 재개 실패");
                }
            }
        });

        // 자동 3초 일시정지 후 재개
        autoSuspendResumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ProcessControl.suspendProcess(processId)) {
                    statusLabel.setText("상태: 프로세스 3초 일시정지 중...");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (ProcessControl.resumeProcess(processId)) {
                                statusLabel.setText("상태: 프로세스 재개됨");
                            } else {
                                statusLabel.setText("상태: 재개 실패");
                            }
                        }
                    }, 3000);  // 3초 후 재개
                } else {
                    statusLabel.setText("상태: 일시정지 실패");
                }
            }
        });
    }

    public static void main(String[] args) {
        GTASuspendGUI gui = new GTASuspendGUI();
        gui.setVisible(true);
    }
}
