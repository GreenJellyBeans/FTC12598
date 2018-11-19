// A strain test by pulling on each end
// Author: Joseph M. Joy
//
EPSILON = 0.001;
testDia = 6; // diameter of the thin, center cylinder
testHeight = 4; // height of thin cylinder
capDia = 12; // diameter of the thicker end-caps
capHeight = 8;
transitionHeight = 3; // Height of truncated transition cone from cap to test

pinHoleDia = 3; // diameter of hole in end-cap for pin
pinHoleCenterHeight = 3;
cordDia = 3; // Nominal diameter of cord (actually it is slightly larger than cord)


// The cord is attached to this end cap - looped over a pin inserted into
// the pin hole. There is one one each side of the test piece.
module endCap() {
    difference() {
        cylinder(d=capDia, capHeight);
        negativeCapSpace();
    }
}


// The composite holes for the pin and cord - designed to be
// subtracted from the end cap solid.
module negativeCapSpace() {
    pinLen = 2*capDia;
    cx = (cordDia + 0.6*pinHoleDia)/2;
    ch = pinHoleCenterHeight + pinHoleDia/2 + cordDia;
    union() {
        translate([0, pinLen/2, pinHoleCenterHeight]) rotate([90, 0, 0]) 
            cylinder(d=pinHoleDia, pinLen);
        translate([-cx, 0, -EPSILON]) cylinder(d=cordDia, ch);
        translate([cx, 0, -EPSILON]) cylinder(d=cordDia, ch);
        translate([-cx, -cordDia/2, -EPSILON]) cube([2*cx, cordDia, ch]);
    }
}


// Truncated cone transition from end cap to the thinner central test cylinder.
module transition() {
    cylinder(d1=capDia, d2=testDia, transitionHeight);
}


// The thin central cylinder - designed to be the piece that fails under
// the test load
module center() {
    cylinder(d=testDia, testHeight);
}


// The final test piece
module pullTestPiece() {
    endCap();
    z1 = capHeight;
    translate([0,0,capHeight]) transition();
    z2 = z1 + transitionHeight;
    translate([0,0, capHeight+transitionHeight]) center();
    z3 = z2 + testHeight;
    translate([0,0,z3 + transitionHeight]) rotate([180,0,0]) transition();
    z4 = z3 + transitionHeight;
    translate([0,0,z4 + capHeight]) rotate([180,0,0]) endCap();
}


$fn = 100;
//endCap();
//transition();
//center();
pullTestPiece();
//negativeCapSpace();
