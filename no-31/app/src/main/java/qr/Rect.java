package qr;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Rect {

    public final Point2d leftTop;
    public final Point2d rightBottom;

    public boolean fillReversed = false;

    public Rect(Point2d center, int totalWidth) {
        if (totalWidth <= 0) {
            throw new IllegalArgumentException("Illegal rectangle");
        }
        if (totalWidth % 2 == 0) {
            throw new IllegalArgumentException("centered Rectangle must have odd width");
        }
        var distance = totalWidth / 2;
        this.leftTop = new Point2d(center.x - distance, center.y - distance);
        this.rightBottom = new Point2d(center.x + distance, center.y + distance);
    }

    public Rect(int x, int y, int width, int height) {
        this(new Point2d(x, y), width, height);
    }

    public Rect(Point2d leftTop, int width, int height) {
        if (width < 0) {
            width = -width;
            leftTop = new Point2d(leftTop.x - width + 1, leftTop.y);
            this.fillReversed = true;
        }
        if (height < 0) {
            height = -height;
            leftTop = new Point2d(leftTop.x, leftTop.y - height + 1);
            this.fillReversed = true;
        }
        this.leftTop = leftTop;
        this.rightBottom = new Point2d(leftTop.x + width - 1, leftTop.y + height - 1);
    }

    public Rect(Point2d leftTop, Point2d rightBottom) {
        this.leftTop = leftTop;
        this.rightBottom = rightBottom;
    }

    public boolean includes(Point2d point) {
        return
                this.leftTop.x <= point.x && point.x <= this.rightBottom.x
                        && this.leftTop.y <= point.y && point.y <= this.rightBottom.y;
    }

    public int x() {
        return this.leftTop.x;
    }
    public int y() {
        return this.leftTop.y;
    }

    public int width() {
        return this.rightBottom.x - this.leftTop.x + 1;
    }

    public int height() {
        return this.rightBottom.y - this.leftTop.y + 1;
    }

    public Optional<Rect> intersection(Rect other) {
        var leftX = Math.max(this.leftTop.x, other.leftTop.x);
        var leftY = Math.max(this.leftTop.y, other.leftTop.y);
        var rightX = Math.min(this.rightBottom.x, other.rightBottom.x);
        var rightY = Math.min(this.rightBottom.y, other.rightBottom.y);
        if (leftX < rightX && leftY < rightY) {
            return Optional.of(new Rect(new Point2d(leftX, leftY), new Point2d(rightX, rightY)));
        } else {
            return Optional.empty();
        }
    }

    public List<Rect> intersection(List<Rect> rectRegions) {
        var intersections = new ArrayList<Rect>();
        for (var r : rectRegions) {
            var intersection = this.intersection(r);
            intersection.ifPresent(intersections::add);
        }
        return intersections;
    }

    public boolean contains(Point2d point) {
        return this.x() <= point.x && point.x < this.x()+this.width()
                && this.y() <= point.y && point.y < this.y() + this.height();
    }

    @Override
    public String toString() {
        return "Rect[ " + this.leftTop + " - " + this.rightBottom + ", w="+this.width()+", h="+this.height()+" ]";
    }

    public Rect translate(int dx, int dy, int dw, int dh) {
        return new Rect(this.x()+dx, this.y() + dy, this.width() + dw, this.height() + dh);
    }

}
