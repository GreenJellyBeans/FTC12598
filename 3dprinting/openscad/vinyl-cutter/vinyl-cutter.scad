// Frame to mount OFLA rotary cutter on a rail and weigh it down
// History:
//  2020 May 1: JMJ Created original design
//
use <../common/extrusions.scad>
$fn = 50;
EPSILON = 0.01;
LARGE = 100;
MM = 25.4; // Inch to mm
hole_dist = 3.5*MM;
base_w = 0.4*MM; // from hole to edge
ro = 0.5*MM;
rod_dia = 4; // To let #10 threaded rod fit


// Back plate
module back_plate() {
    extra_below = 0.5*MM; // lower part is 1"
    t = 0.5;
    r = rod_dia/2;
    hole_y = base_w + extra_below;
    difference() {
        union() {

            wbase = 2*base_w + hole_dist;
            hbase = 2*base_w + extra_below;
            oblong(wbase, hbase, ro, t);
        }
        translate([base_w, hole_y, -EPSILON]) 
            cylinder(r=r, h=2*t);
        translate([base_w + hole_dist, hole_y, -EPSILON]) 
            cylinder(r=rod_dia/2, h=2*t);
    }
}

// Front plate
module front_plate() {
    extra_below = 0.25*MM; // lower part is 1"
    big_r = 1.25*MM; // Arc above
    t = 0.5;
    hole_y = base_w + extra_below;
    big_hole_dia = 1*MM;
    big_hole_h  = 1.25*MM;
    difference() {
        union() {

            wbase = 2*base_w + hole_dist;
            hbase = 2*base_w + extra_below;
            oblong(wbase, hbase, ro, t);
            translate([base_w + hole_dist/2, hole_y, 0]) {
                difference() {
                    cylinder(r=big_r, h = t);
                    translate([-big_r, -2*big_r, -EPSILON])
                        cube([2*big_r, 2*big_r, 2*t]);
                }
            }
        }
        translate([base_w, hole_y, -EPSILON]) 
            cylinder(r=rod_dia/2, h=2*t);
        translate([base_w + hole_dist, hole_y, -EPSILON]) 
            cylinder(r=rod_dia/2, h=2*t);
        bdd = big_hole_dia;
        translate([base_w + hole_dist/2 -bdd/2, hole_y - bdd/2, -EPSILON])
            oblong(bdd, bdd, bdd/2, 2*t);

    }
}


//wb = 10;
//hb = 20;
//ro = 5;

//oblong(wb, hb, ro, t);
//back_plate();
//translate([0, 5*base_w, 0])
    front_plate();