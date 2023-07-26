package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class CallTestAPIAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get the current project and editor
        Project project = e.getProject();
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        // Make sure project and editor are not null
        if (project == null || editor == null) {
            return;
        }

        // Create a new thread to execute the API call
        new Thread(() -> {
            // Replace 'YOUR_TEST_API_URL' with your actual test API endpoint
            String apiUrl = "https://jsonplaceholder.typicode.com/todos/1";

            // Make the API call using Apache HttpClient (you can use any other library of your choice)
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(apiUrl);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                String apiResponse = EntityUtils.toString(entity);

                // Wrap the document update code in a Runnable to execute on the EDT
                Runnable documentUpdateRunnable = () -> {
                    Document document = editor.getDocument();
                    document.setText(apiResponse);

                    // Refresh the editor to display the changes
                    EditorFactory.getInstance().refreshAllEditors();
                };

                // Execute the document update on the EDT
                Application application = ApplicationManager.getApplication();
                application.invokeLater(documentUpdateRunnable);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
