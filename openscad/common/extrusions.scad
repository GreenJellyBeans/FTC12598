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
    ow = 2*wt; // wide enough to 
    oh = 2*(hb+ht); // high enough to descend below the shape

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
module O_channel(wo, ho, wi, hi, xoff, yoff, ro, ri, t) {
    difference() {
        oblong(wo, ho, ro, t);
        translate([xoff, yoff, -EPSILON]) oblong(wi, hi, ri, t+2*EPSILON);
    }
}


// A rectangular prism that fits within {w} x {h} and
// has rounded corners with radius {r} on all 4 sides.
// The actual radius may be reduced so keep the oblong
// viable. {t} is the thickness (in z).
module oblong(w, h, r, t) {
    r = min(r, w/2-EPSILON, h/2-EPSILON);
    translate([r, r, 0])
    linear_extrude(height = t, center = false, convexity = 10) {
        offset(r = r) square([w-2*r, h-2*r]);
    }
}


//
// Example code
//
wb = 10;
hb = 20;
wt = 40;
ht = 30;
t = 5;
r = 5;
ri = 1;
xoff = 10;
oblong(wb, hb, r, t);
translate([wb+5, 0, 0]) T_channel(wb, hb, wt, ht, xoff, r, t);
translate([wb+wt+10, 0, 0]) O_channel(wt, hb+ht, wb, 10*hb, xoff, xoff, r, ri, t);


