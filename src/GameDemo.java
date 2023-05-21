import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class GameDemo extends JFrame {
	private JPanel contentPanel; // 視窗的主畫面
	private Game game = new Game();
	private GameControlPanel controlPanel;
	private Timer timer;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					GameDemo frame = new GameDemo();
					frame.startGame(); // 启动游戏
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void startGame() {
		timer.start(); // 启动定时器
	}

	public GameDemo() {
		this.game = new Game();
		this.controlPanel = new GameControlPanel(game);

		setTitle("PipeMania");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 200, 1000, 600);

		contentPanel = new JPanel();
		contentPanel.setBackground(new Color(255, 255, 255));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);

		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 650, 325, 0 };
		gbl_contentPane.rowHeights = new int[] { 550, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPane);

		GridBagConstraints gbc_GameMapPanel = new GridBagConstraints();
		gbc_GameMapPanel.fill = GridBagConstraints.BOTH;
		gbc_GameMapPanel.gridx = 0;
		gbc_GameMapPanel.gridy = 0;
		contentPanel.add(game.getPanel(), gbc_GameMapPanel);

		GridBagConstraints gbc_OperatePanel = new GridBagConstraints();
		gbc_OperatePanel.fill = GridBagConstraints.BOTH;
		gbc_OperatePanel.gridx = 1;
		gbc_OperatePanel.gridy = 0;
		contentPanel.add(controlPanel.ControlPanel, gbc_OperatePanel);

		timer = new Timer((1000 / 30), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game.render(); // 更新游戏画面
				controlPanel.lastRoundButton.setEnabled(game.lastIsReady);
				controlPanel.nextRoundButton.setEnabled(game.nextIsReady);
			}
		});

	}

}
