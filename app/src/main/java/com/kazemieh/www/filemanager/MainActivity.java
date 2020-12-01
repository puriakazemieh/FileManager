package com.kazemieh.www.filemanager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String addres;
    static RecyclerView recyclerView;
    static ConstraintLayout cl_MainActivity_ccdr, cl_MainActivity_pc;
    String pathc;
    String namec;
    int statusc;
    Context context;
    public static File listfile[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        cl_MainActivity_ccdr = findViewById(R.id.cl_MainActivity_ccdr);
        cl_MainActivity_pc = findViewById(R.id.cl_MainActivity_pc);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            recyclerView = findViewById(R.id.rv_MainActivity_showlistfile);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            String path = Environment.getExternalStorageDirectory().toString();
            refresh(path, this);
        }

        LinearLayout ll_copy = findViewById(R.id.ll_MainActivity_copy);
        LinearLayout ll_cut = findViewById(R.id.ll_MainActivity_cut);
        LinearLayout ll_delete = findViewById(R.id.ll_MainActivity_delete);
        LinearLayout ll_rename = findViewById(R.id.ll_MainActivity_rename);
        LinearLayout ll_paste = findViewById(R.id.ll_MainActivity_paste);
        LinearLayout ll_cancel = findViewById(R.id.ll_MainActivity_cansel);

        ll_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cl_MainActivity_pc.setVisibility(View.VISIBLE);
                pathc = addres;
                namec = Adapter.na;
                statusc = 1;

            }
        });
        ll_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cl_MainActivity_pc.setVisibility(View.VISIBLE);
                pathc = addres;
                namec = Adapter.na;
                statusc = 2;
            }
        });
        ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setCancelable(true);
                builder.setTitle("آیا مطمئن هستید ؟");
                builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for (int i = 0; i < listfile.length; i++) {
                            if (Adapter.selection[i]) {
                                File file = new File(addres + "/" + Adapter.naa[i]);
                                delete(file);
                            }
                        }
                        refresh(addres, context);
                        cl_MainActivity_ccdr.setVisibility(View.GONE);
                        cl_MainActivity_pc.setVisibility(View.GONE);
                    }
                });
                builder.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
        ll_rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rename();
            }
        });
        ll_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cl_MainActivity_pc.setVisibility(View.GONE);
                cl_MainActivity_ccdr.setVisibility(View.GONE);
                for (int i = 0; i <listfile.length ; i++) {
                   if (Adapter.naa!=null){
                        paste(getApplicationContext(),Adapter.naa[i]);
                  }
                }

            }
        });
        ll_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cl_MainActivity_pc.setVisibility(View.GONE);
                cl_MainActivity_ccdr.setVisibility(View.GONE);
            }
        });


    }

    public static void refresh(String path, Context context) {
        addres = path;
        File file = new File(path);
        listfile = file.listFiles();

        Adapter.selection = new boolean[listfile.length];
        Adapter.naa = new String[listfile.length];

        List<DataModel> dataModels = new ArrayList<>();
        for (int i = 0; i < listfile.length; i++) {
            dataModels.add(new DataModel(listfile[i].getName()));
        }
        Adapter adapter = new Adapter(context, dataModels);
        recyclerView.setAdapter(adapter);
    }

    public void back() {
        int p = 0;
        for (int i = addres.length() - 1; i >= 0; i--) {
            if (addres.charAt(i) == '/') {
                p = i;
                break;
            }
        }
        //      storege/emulated/0/alarms
        addres = addres.substring(0, p);
        refresh(addres, this);
    }

    @Override
    public void onBackPressed() {
        if (addres.equals(Environment.getExternalStorageDirectory().toString())) {
            finish();
        } else {
            back();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "ممنون که مجوز را صادر کردید", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "برای اجرای درست مجوز را باید صادر کنید", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void copy(File inputfile, File outputfile) throws IOException {
        if (inputfile.isDirectory()) {
            if (!outputfile.exists()) {
                outputfile.mkdir();
            }
            String child[] = inputfile.list();
            for (int i = 0; i < inputfile.listFiles().length; i++) {

                // filein = adobe iil
                //filein= adobe phs
                //filein= comon
                File filein = new File(inputfile, child[i]);
                File fileout = new File(outputfile, child[i]);
                copy(filein, fileout);
            }

        } else {
            InputStream inputStream = new FileInputStream(inputfile);
            OutputStream outputStream = new FileOutputStream(outputfile);
            int len;
            byte[] bufer = new byte[1024];
            while ((len = inputStream.read()) > 0) {
                outputStream.write(bufer, 0, len);
            }
            inputStream.close();
            outputStream.close();

        }
    }

    public void paste(final Context context, final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        if (statusc == 1) {
            builder.setTitle("آیا مایل به کپی کردن هستید؟");
            builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File inputfile = new File(pathc + "/" + name);
                    File outputfile = new File(addres + "/" + name);
                    try {
                        copy(inputfile, outputfile);
                        refresh(addres, context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

        } else if (statusc == 2) {
            builder.setTitle("آیا مایل به جابه جایی هستید؟");
            builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File moveinputcut = new File(pathc + "/" + namec);
                    File moveoutputcut = new File(addres + "/" + namec);
                    boolean bcut = moveinputcut.renameTo(moveoutputcut);
                    if (bcut) {
                        refresh(addres, context);
                    } else {
                        Toast.makeText(context, "جابه جایی انجام نشد", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            builder.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void Rename() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.rename, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        final EditText et_rename = view.findViewById(R.id.et_MainActivty_rename);
        Button b_yes = view.findViewById(R.id.b_MainActivity_yes);
        Button b_no = view.findViewById(R.id.b_MainActivity_no);
        et_rename.setText(Adapter.na);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        b_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File renamefile = new File(addres + "/" + Adapter.na);
                File finalrenamefile = new File(addres + "/" + et_rename.getText().toString());
                boolean brename = renamefile.renameTo(finalrenamefile);
                if (brename) {
                    refresh(addres, getApplicationContext());
                    Toast.makeText(MainActivity.this, "نام فایل تغییر کرد", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(MainActivity.this, "نام فایل تغییر نکرد", Toast.LENGTH_SHORT).show();
                }
                alertDialog.cancel();
            }
        });
        b_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        cl_MainActivity_ccdr.setVisibility(View.GONE);


    }

    public void delete(File file) {
        if (file.isDirectory()) {
            if (file.list().length == 0) {
                file.delete();
            } else {
                String[] child = file.list();
           /*     for (int i = 0; i <file.list().length ; i++) {
                    File filedel=new File(file,child[i]);
                    delete(filedel);
                }*/
                for (String childs : child) {
                    File filedel = new File(file, childs);
                    delete(filedel);
                }
                if (file.list().length == 0) {
                    file.delete();
                }
            }
        } else {
            file.delete();
        }
    }

}