//
// Common extrusions
// Author: Joseph M. Joy FTC 12598 mentor
//
EPSILON = 0.01; // For moving things to properly punch out holes

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
// {ro} curve of convex corners (not shown above)
// {ri} is the radius of two concave curves
// curve shown as diagonals above.
// t is the thickness of the plate - in
// the z direction.
// Origin is the left hand corner
// of the bounding rectangle.
//
// WARNING: If {ro} and {ri} are too large it will generate sharp
// extrusion artifacts because the straight surfaces are not large to accomodate
// the inner-radius curves.
// 
module T_channelOld(wb, hb, wt, ht, xoff, r, t) {
    ow = 2*wt; // wide enough to 
    oh = 2*(hb+ht); // high enough to descend below the shape

    difference() {
        cube([wt, hb+ht, t]);
        translate([xoff-ow,hb-oh,-EPSILON]) oblong(ow, oh, r, t+2*EPSILON);
        translate([xoff+wb,hb-oh,-EPSILON]) oblong(ow, oh, r, t+2*EPSILON);
    }
}


module T_channel(wb, hb, wt, ht, xoff, ro, ri, t) {
    $fn = 100;
    
    // ow and oh are used to create cutout patterns
    ow = 2*wt; // wide enough to extend to the left and right of shape
    oh = 2*(hb+ht); // high enough to descend below the shape
    
    // Dimensions of the plate that will be cut out - it is just
    // big enough so that the inner-radii can be cut out, no more.
    wp = wb + 2*ri + 2*EPSILON; // dimensions of plate to be cut out
    hp = ro + 2*EPSILON;
    xoffp = xoff - ri - EPSILON;
    yoffp = hb - ro - EPSILON;
    difference() {
        translate([xoffp, yoffp, 0]) cube([wp, hp, t]);
        translate([xoff-ow,hb-oh,-EPSILON]) oblong(ow, oh, ri, t+2*EPSILON);
        translate([xoff+wb,hb-oh,-EPSILON]) oblong(ow, oh, ri, t+2*EPSILON);
    }
    translate([xoff, 0, 0]) oblong(wb, hb, ro, t);
    translate([0, hb, 0]) oblong(wt, ht, ro, t);
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

// A cube that fits within ({w}, {h}, {d}) and has
// all corners rounded with radius r.
module rounded_cube(w, h, d, r) {
        r = 1; // min(r, w/2-EPSILON, h/2-EPSILON, d/2-EPSILON);
        di = 2*r;
    translate([r, r, r]) {
        minkowski() {
            echo(di);
            cube([w-di, h-di, d-di]);
            sphere(r=r);
        }
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
ro = 5;
ri = 2;
xoff = 10;
move = 30;
oblong(wb, hb, ro, t);
translate([0, move, 0]) rounded_cube(wb, hb, t, 2);//, $fn=100);
translate([move, 0, 0]) T_channel(wb, hb, wt, ht, xoff, ro, ri, t);
translate([move, 2*move, 0]) O_channel(wt, hb+ht, wb, 10*hb, xoff, xoff, ro, ri, t);


