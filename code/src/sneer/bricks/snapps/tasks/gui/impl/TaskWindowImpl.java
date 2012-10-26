package sneer.bricks.snapps.tasks.gui.impl;

import static basis.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import basis.lang.Consumer;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.tasks.gui.TaskWindow;
import sneer.bricks.snapps.tasks.impl.Task;
import sneer.bricks.snapps.tasks.impl.TaskMessage;

public class TaskWindowImpl extends JFrame implements TaskWindow {

	private boolean _isGuiInitialized = false;
	
	private JLabel _newTaskLB;
	private JTextField _newTaskTF;
	private JTextArea _tasksTA;
	private JButton _createBtn;

	@SuppressWarnings("unused")
	private WeakContract _refToAvoidGc;
	
	{
		addTaskAction();
		subscribeToReceiveTaskMessages();
	}
	
	private void addTaskAction() {
		my(ContactActionManager.class).addContactAction(new ContactAction(){
			@Override public boolean isEnabled() { return true; }
			@Override public boolean isVisible() { return true; }
			@Override public String caption() { return "Tasks";}
			@Override public void run() { open(); }
			@Override public int positionInMenu() { return 100;	};
		}, true);
	}
	
	private void open() {
		if(!_isGuiInitialized) {
			_isGuiInitialized = true;
			initGui();
		}
		setLocationRelativeTo(my(ContactActionManager.class).baseComponent());
		setVisible(true);
	}
	
	private void initGui() {
		setTitle("Tasks");
		setResizable(false);
		
		_newTaskLB = new JLabel("New Task");
		_newTaskTF = new JTextField();
		_tasksTA = new JTextArea();
		_createBtn = new JButton("Ok" );

		_createBtn.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent ignored) {
			addTask(_newTaskTF.getText());
		}});
		
		setBorderLayout(); 

		this.setSize(330, 220);
	}

	private void setBorderLayout() {
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		contentPane.add(_tasksTA,  BorderLayout.CENTER);
		
		JPanel newTaskPanel = new JPanel(new BorderLayout());
		newTaskPanel.add(_newTaskLB,  BorderLayout.PAGE_START);
		newTaskPanel.add(_newTaskTF,  BorderLayout.CENTER);
		newTaskPanel.add(_createBtn,  BorderLayout.LINE_END);
		
		contentPane.add(newTaskPanel,  BorderLayout.PAGE_END);
	}
	
	
	private void subscribeToReceiveTaskMessages() {
		_refToAvoidGc = my(TupleSpace.class).addSubscription(TaskMessage.class, new Consumer<TaskMessage>() { @Override public void consume(final TaskMessage message) {
			_tasksTA.append(message.getTask().toString());
		}});
	
    }
	
	private void addTask(String text) {
		Task task = new Task(text);
		my(TupleSpace.class).add(new TaskMessage(task));
	}
	
}
