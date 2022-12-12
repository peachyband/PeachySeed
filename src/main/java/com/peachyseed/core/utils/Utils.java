package com.peachyseed.core.utils;

import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {

    public static FloatBuffer StoreDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static IntBuffer StoreDataInIntBuffer(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static String LoadResource(String filename) throws Exception {
        String result;
        try(InputStream in = Utils.class.getResourceAsStream(filename);
            Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    public static List<String> ReadAllLines(String fileName) {
        List<String> list = new ArrayList<>();
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(
                Class.forName(Utils.class.getName()).getResourceAsStream(fileName)))) {
            String line;
            while ((line = buffer.readLine()) != null) {
                list.add(line);
            }
        }
        catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
        return list;
    }
}
