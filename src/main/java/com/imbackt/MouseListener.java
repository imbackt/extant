package com.imbackt;

import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener INSTANCE;
    private double scrollX;
    private double scrollY;
    private double xPosition;
    private double yPosition;
    private double lastX;
    private double lastY;
    private boolean mouseButtonPressed[] = new boolean[3];
    private boolean isDragging;

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPosition = 0.0;
        this.yPosition = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static MouseListener getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MouseListener();
        }
        return INSTANCE;
    }

    public static void mousePositionCallback(long window, double xPosition, double yPosition) {
        getInstance().lastX = getInstance().xPosition;
        getInstance().lastY = getInstance().yPosition;
        getInstance().xPosition = xPosition;
        getInstance().yPosition = yPosition;
        getInstance().isDragging = getInstance().mouseButtonPressed[0] || getInstance().mouseButtonPressed[1] || getInstance().mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (button < getInstance().mouseButtonPressed.length) {
                getInstance().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            if (button < getInstance().mouseButtonPressed.length) {
                getInstance().mouseButtonPressed[button] = false;
                getInstance().isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        getInstance().scrollX = xOffset;
        getInstance().scrollY = yOffset;
    }

    public static void endFrame() {
        getInstance().scrollX = 0;
        getInstance().scrollY = 0;
        getInstance().lastX = getInstance().xPosition;
        getInstance().lastY = getInstance().yPosition;
    }

    public static float getX() {
        return (float) getInstance().xPosition;
    }

    public static float getY() {
        return (float) getInstance().yPosition;
    }

    public static float getDx() {
        return (float) (getInstance().lastX - getInstance().xPosition);
    }

    public static float getDy() {
        return (float) (getInstance().lastY - getInstance().yPosition);
    }

    public static float getScrollX() {
        return (float) getInstance().scrollX;
    }

    public static double getScrollY() {
        return (float) getInstance().scrollY;
    }

    public static boolean isDragging() {
        return getInstance().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        if (button > getInstance().mouseButtonPressed.length) {
            return getInstance().mouseButtonPressed[button];
        } else {
            return false;
        }
    }
}
