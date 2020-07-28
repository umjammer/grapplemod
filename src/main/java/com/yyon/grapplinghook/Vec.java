
package com.yyon.grapplinghook;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;


public class Vec {

    public double x;
    public double y;
    public double z;

    public Vec(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec(Vec3d vec3d) {
        this.x = vec3d.x;
        this.y = vec3d.y;
        this.z = vec3d.z;
    }

    public Vec(Vec vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public Vec3d toVec3d() {
        return new Vec3d(this.x, this.y, this.z);
    }

    public static Vec positionVec(Entity e) {
        return new Vec(e.getPos().x, e.getPos().y, e.getPos().z);
    }

    public static Vec motionVec(Entity e) {
        return new Vec(e.getVelocity());
    }

    public Vec add(Vec v2) {
        return new Vec(this.x + v2.x, this.y + v2.y, this.z + v2.z);
    }

    public void addIp(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void addIp(Vec v2) {
        this.x += v2.x;
        this.y += v2.y;
        this.z += v2.z;
    }

    public Vec sub(Vec v2) {
        return new Vec(this.x - v2.x, this.y - v2.y, this.z - v2.z);
    }

    public void subIp(Vec v2) {
        this.x -= v2.x;
        this.y -= v2.y;
        this.z -= v2.z;
    }

    public Vec rotateYaw(double a) {
        return new Vec(this.x * Math.cos(a) - this.z * Math.sin(a), this.y, this.x * Math.sin(a) + this.z * Math.cos(a));
    }

    public Vec rotatePitch(double pitch) {
        return new Vec(this.x,
                       this.y * Math.cos(pitch) + this.z * Math.sin(pitch),
                       this.z * Math.cos(pitch) - this.y * Math.sin(pitch));
    }

    public Vec mult(double changeFactor) {
        return new Vec(this.x * changeFactor, this.y * changeFactor, this.z * changeFactor);
    }

    public void multIp(double changeFactor) {
        this.x *= changeFactor;
        this.y *= changeFactor;
        this.z *= changeFactor;
    }

    public double length() {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
    }

    public Vec normalize() {
        return this.mult(1.0 / this.length());
    }

    public void normalizeIp() {
        this.multIp(1.0 / this.length());
    }

    public double dot(Vec v2) {
        return this.x * v2.x + this.y * v2.y + this.z * v2.z;
    }

    public Vec changeLen(double l) {
        double oldl = this.length();
        if (oldl != 0) {
            double changeFactor = l / oldl;
            return this.mult(changeFactor);
        } else {
            return this;
        }
    }

    public void changeLenIp(double l) {
        double oldl = this.length();
        if (oldl != 0) {
            double changefactor = l / oldl;
            this.multIp(changefactor);
        }
    }

    public Vec proj(Vec v2) {
        Vec v3 = v2.normalize();
        double dot = this.dot(v3);
        return v3.changeLen(dot);
    }

    public double distAlong(Vec v2) {
        Vec v3 = v2.normalize();
        return this.dot(v3);
    }

    public Vec removeAlong(Vec v2) {
        return this.sub(this.proj(v2));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(this.x);
        sb.append(",");
        sb.append(this.y);
        sb.append(",");
        sb.append(this.z);
        sb.append(">\n");
        return sb.toString();
    }

    public Vec add(double x, double y, double z) {
        return new Vec(this.x + x, this.y + y, this.z + z);
    }

    public double getYaw() {
        Vec norm = this.normalize();
        return Math.toDegrees(-Math.atan2(norm.x, norm.z));
    }

    public double getPitch() {
        Vec norm = this.normalize();
        return Math.toDegrees(-Math.asin(norm.y));
    }

    public Vec cross(Vec b) {
        return new Vec(this.y * b.z - this.z * b.y, this.z * b.x - this.x * b.z, this.x * b.y - this.y * b.x);
    }
}
