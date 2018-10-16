//
// Common extrusions
// Author: Joseph M. Joy FTC 12598 mentor
//
EPSILON = 0.001; // For moving things to properly punch out holes

// Makes a flat (horizontal) "T":
//
// --------------
// |wtXht     --|
// ----\     /---
//      |wbX|
//      |hb |
//      -----
//<xoff>
//
// It could also be an inverted L shape
// if the lower (vertical) bar is wide enough to extend beyond the right 
// hand side of the upper (horizontal) bar.
//
// {xoff} is horizontal distance to the start of the bar.
// {r} is the radius of the
// curve shown as diagonals above.
// t is the thickness of the plate - in
// the z direction.
// Origin is the left hand corner
// of the bounding rectangle.
// 
module T_channel(wb, hb, wt, ht, xoff, r, t) {
    ow = 10*wb;
    oh = 10*hb;

    difference() {
        cube([wt, hb+ht, t]);
        translate([xoff-ow,hb-oh,-EPSILON]) oblong(ow, oh, r, t+2*EPSILON);
        translate([xoff+wb,hb-oh,-EPSILON]) oblong(ow, oh, r, t+2*EPSILON);
    }
}


// Makes a flat (horizontal) "O":
//
// /-------------\
// |             |
// | /--------\  |
// | |         | |
// | |         | |
// | |         | |
// | \         / |
// |  ---------  |
// \------------/
//
//
// It could also be a U or L
// if the oblong hole extends outside the outer
// boundary.
//
// ({xoff},{yoff} is origin of bounding-rectangle of the oblong hole
// {(wo}, {ho}) is the bounding box of the outer oblong.
// {(wi}, {hi}) is the bounding box of the inner (hole) oblong.
// {r} is the radius of the
// curve shown as diagonals above.
// t is the thickness of the plate - in
// the z direction.
// Origin is the left hand corner
// of the bounding rectangle.
// 
module O_channel(wo, ho, wi, hi, xoff, yoff, r, t) {
    difference() {
        oblong(wo, ho, r, t);
        translate([xoff, yoff, -EPSILON]) oblong(wi, hi, r, t+2*EPSILON);
    }
}


// A rectangular prism that fits within {w} x {h} and
// has rounded corners with radius {r} on all 4 sides.
// The actual radius may be reduced so keep the oblong
// viable. {t} is the thickness (in z).
module oblong(w, h, r, t) {
    r = min(r, w/2-EPSILON, h/2-EPSILON);
    translate([r, r, 0])
    minkowski() {
        cube([w-2*r, h-2*r, t]);
        cylinder(2*t, r, r);
    }
}


wb = 5;
hb = 20;
wt = 40;
ht = 30;
t = 2;
r = 5;
ri = 5;
xoff = 10;
//T_channel(wb, hb, wt, ht, xoff, r, t);
O_channel(wt, hb+ht, wb, hb+ht, xoff, xoff, ri, t);
//oblong(11, 40, 5, 2);

