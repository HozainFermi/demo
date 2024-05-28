package com.lab1.demo.Controller;

import com.lab1.demo.HelloApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FolderVIewController implements Initializable {

    @FXML
    public FlowPane FilesPane;
    public MenuItem NewInnerFile;
    public MenuItem NewInnerFolder;
    public Label UIpath;

    String mainpath = HelloController.class.getProtectionDomain().getCodeSource().getLocation().getPath();

   static String fn;
   public String path;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FilesPane.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent mouseDragEvent) {
                VBox vb = (VBox)mouseDragEvent.getGestureSource();
                vb.setStyle("-fx-border-color: transparent");
            }
        });

        ArrayList<Node> nodes = new ArrayList<>();
        mainpath=mainpath.replace("demo-1.0-SNAPSHOT-shaded.jar","");
        path=fn;
        Thread thr = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<String>  listfiles=  ShellExec.ExecCommand("ls "+path+" -p | grep -v /");
                    ArrayList<String>   listfolders =ShellExec.ExecCommand("ls "+"-d"+" "+path+"/*/");
                    if(!listfiles.isEmpty()) {
                        for (String resp : listfiles) {

                            if(resp.contains(" ")) {
                                String[] response = resp.split(" ");
                                resp="";
                                String sub="";
                                for(int i=0;i<response.length;i++){
                                    sub=response[i];
                                    if(i==response.length-1){
                                        resp+=sub;
                                    }
                                    else{
                                        resp+=sub+"\\ ";
                                    }

                                }
                            }
                            nodes.add(CreateNew(resp));
                        }
                    }
                    if(!listfolders.isEmpty()){
                        for(String st : listfolders){
                            st = st.replace(path+"/","");
                            st = st.replace("/","");

                            if(st.contains(" ")) {
                                String[] response = st.split(" ");
                                st="";
                                String sub="";
                                for(int i=0;i<response.length;i++){
                                    sub=response[i];
                                    if(i== response.length-1){
                                        st+=sub;
                                    }
                                    else{
                                        st+=sub+"\\ ";
                                    }
                                }
                            }
                            nodes.add(CreateNewFolder(st));
                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thr.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FilesPane.getChildren().addAll(nodes);
                UIpath.setText(path);
            }
        });

    }

    public  Node CreateNewFolder(String fn) {
        VBox v = new VBox();
        ImageView iv = new ImageView();
        Label filename = new Label();
        Insets insets = new Insets(10, 5, 0, 0);
        v.setPadding(insets);

        File fl = new File(mainpath+"folder-documents-icon(1).png");
        Image img = new Image(fl.toURI().toString());
        v.setPrefHeight(Region.USE_COMPUTED_SIZE);
        v.setPrefWidth(60);
        iv.setFitHeight(41.0);
        iv.setFitWidth(41.0);
        iv.setPickOnBounds(true);
        iv.setPreserveRatio(true);
        iv.setImage(img);
        v.getChildren().add(iv);
        filename.setText(fn);
        filename.setWrapText(true);
        v.setId(fn+"<folder>");

        ContextMenu cm = new ContextMenu();

        javafx.scene.control.MenuItem open = new javafx.scene.control.MenuItem("Open");
        javafx.scene.control.MenuItem rename = new MenuItem("Rename");
        javafx.scene.control.MenuItem delete = new javafx.scene.control.MenuItem("Delete");
        javafx.scene.control.MenuItem copy = new MenuItem("Copy");

        cm.getItems().addAll(open,rename, copy, delete);
        filename.setContextMenu(cm);
        v.getChildren().add(filename);
        //v.setId(fn);  // <folder>

        v.setOnMouseDragEntered(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent mouseDragEvent) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        v.setStyle("-fx-background-color: rgba(13, 137, 209, 0.63)");
                    }
                });
            }
        });

        v.setOnMouseDragExited(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent mouseDragEvent) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        v.setStyle("-fx-background-color: transparent");
                    }
                });
            }
        });

        v.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent mouseDragEvent) {
                VBox vBox = (VBox)mouseDragEvent.getGestureSource();
                String name = (String)vBox.getId();
                if(name.contains("<file>")) {
                    name = name.replace("<file>", "");
                }
                if(name.contains("<folder>")) {
                    name = name.replace("<folder>", "");
                }
                FilesPane.getChildren().remove(vBox);
                try {
                    ShellExec.ExecCommand("mv "+path+"/"+name+" "+path+"/"+filename.getText());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        FilesPane.getChildren().remove(v);
                        v.getChildren().removeAll();
                    }
                });
                // v.getChildren().removeAll();
                Thread rn = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<String> resp =  ShellExec.ExecCommand("rm -r "+path+"/"+ filename.getText());
                            for(String ln : resp){
                                System.out.println(ln);
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                rn.start();
            }
        });

        copy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Node copy;
                copy=CreateNewFolder(fn+"COPY");
                copy.setId(v.getId()+"COPY");

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        FilesPane.getChildren().add(copy);
                    }
                });

                Thread thr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<String> resp =  ShellExec.ExecCommand("cp -r "+path+"/"+filename.getText()+" "+path+"/"+filename.getText()+"COPY");
                            for(String ln : resp){
                                System.out.println(ln);
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                thr.start();

            }
        });

        open.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    FolderVIewController.fn =path+"/"+fn;
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("View/FolderView.fxml"));
                    Parent roo = (Parent) fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setTitle(filename.getText());
                    stage.setScene(new Scene(roo));
                    stage.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        rename.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                AnchorPane root = new AnchorPane();
                Scene scene = new Scene(root);
                root.setPrefHeight(30);
                root.setPrefWidth(210);

                TextField tx = new TextField();
                tx.setPrefHeight(14);
                tx.setPrefWidth(157);
                tx.setLayoutY(4);

                Button btn = new Button();
                btn.setLayoutY(4);
                btn.setLayoutX(162);
                btn.setPrefHeight(24);
                btn.setPrefWidth(37);
                btn.setText("Ok");
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        Thread tr = new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    ShellExec.ExecCommand("mv "+path+"/"+filename.getText()+" "+path+"/"+tx.getText());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                        tr.start();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                filename.setText(tx.getText());
                            }
                        });
                        Stage st = (Stage) btn.getScene().getWindow();
                        st.hide();
                    }
                });

                root.getChildren().addAll(tx,btn);

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
            }
        });

        return v;
    }


    public  Node CreateNew(String fn) {

        VBox v = new VBox();
        ImageView iv = new ImageView();
        Label filename = new Label();
        Insets insets = new Insets(10, 5, 0, 0);
        v.setPadding(insets);

        File fl = new File(mainpath+"copy_paste_document_file_1557.png");
        Image img = new Image(fl.toURI().toString());
        v.setPrefHeight(Region.USE_COMPUTED_SIZE);
        v.setPrefWidth(60);
        iv.setFitHeight(41.0);
        iv.setFitWidth(41.0);
        iv.setPickOnBounds(true);
        iv.setPreserveRatio(true);
        iv.setImage(img);
        v.getChildren().add(iv);
        filename.setText(fn);
        filename.setWrapText(true);
        v.setId(fn+"<file>");

        ContextMenu cm = new ContextMenu();
        javafx.scene.control.MenuItem open = new javafx.scene.control.MenuItem("Open");
        javafx.scene.control.MenuItem rename = new javafx.scene.control.MenuItem("Rename");
        javafx.scene.control.MenuItem delete = new javafx.scene.control.MenuItem("Delete");
        javafx.scene.control.MenuItem copy = new MenuItem("Copy");

        cm.getItems().addAll(open,rename, copy, delete);
        filename.setContextMenu(cm);
        v.getChildren().add(filename);

        v.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                v.setStyle("-fx-border-color: black");
                v.startFullDrag();

            }
        });

        v.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent mouseDragEvent) {
                v.setStyle("-fx-border-color: transparent");
            }
        });

        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        FilesPane.getChildren().remove(v);
                        v.getChildren().removeAll();
                    }
                });
                // v.getChildren().removeAll();
                Thread rn = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<String> resp =  ShellExec.ExecCommand("rm "+path+"/"+filename.getText());
                            for(String ln : resp){
                                System.out.println(ln);
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                rn.start();

            }
        });

        copy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Node copy;
                copy=CreateNew(fn+"COPY");
                copy.setId(v.getId()+"COPY");

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        FilesPane.getChildren().add(copy);
                    }
                });

                Thread thr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<String> resp =  ShellExec.ExecCommand ("cp "+path+"/"+filename.getText()+" "+path+"/"+filename.getText()+"COPY");
                            for(String ln : resp){
                                System.out.println(ln);
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                thr.start();

            }
        });

        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                Thread tr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ShellExec.ExecCommand("xdg-open "+path+"/"+filename.getText());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                tr.start();

            }
        });

        rename.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                AnchorPane root = new AnchorPane();
                Scene scene = new Scene(root);
                root.setPrefHeight(30);
                root.setPrefWidth(210);

                TextField tx = new TextField();
                tx.setPrefHeight(14);
                tx.setPrefWidth(157);
                tx.setLayoutY(4);

                Button btn = new Button();
                btn.setLayoutY(4);
                btn.setLayoutX(162);
                btn.setPrefHeight(24);
                btn.setPrefWidth(37);
                btn.setText("Ok");
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        Thread tr = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ShellExec.ExecCommand("mv "+path+"/"+filename.getText()+" "+path+"/"+tx.getText());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                        tr.start();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                //FilesPane.getScene().getWindow().setWidth(FilesPane.getScene().getWidth()+0.001);
                                filename.setText(tx.getText());
                            }
                        });
                        Stage st = (Stage) btn.getScene().getWindow();
                        st.hide();
                    }
                });

                root.getChildren().addAll(tx,btn);

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
            }
        });

        return v;
    }

   static public void Getfn(String fna){
        fn=fna;
    }


    public void OnCreateNewInnerFile(ActionEvent event) throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FilesPane.getChildren().add(CreateNew("NewCreatedFile"));
            }
        });
        ShellExec.ExecCommand("touch "+path+"/"+"NewCreatedFile");

    }

    public void OnCreateNewInnerFolder(ActionEvent event) throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FilesPane.getChildren().add(CreateNewFolder("NewCreatedFolder"));
            }
        });
        ShellExec.ExecCommand("mkdir "+path+"/"+"NewCreatedFolder");
    }
}
