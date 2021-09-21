//
//  SRXTabBar.m
//  ReactNativeNavigation
//
//  Created by Wei Liang Tan on 26/5/19.
//  Copyright © 2019 Wix. All rights reserved.
//

#import "SRXTabBar.h"

@implementation SRXTabBar {
	CALayer *currentShapeLayer;
}

// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
  
  [self addShape];
//  [self setBackgroundColor:[UIColor colorWithWhite:1 alpha:0]];
  
  [self.layer setShadowColor:[UIColor blackColor].CGColor];
  [self.layer setShadowOpacity:0.3];
  [self.layer setShadowRadius:1.0];
  [self.layer setShadowOffset:CGSizeMake(0, 2)];
//  self.layer.backgroundColor = [UIColor clearColor].CGColor;
}

-(void)addShape {
	CAShapeLayer * shapeLayer = [CAShapeLayer new];
	shapeLayer.path = [self createPath];
	shapeLayer.strokeColor = [UIColor colorWithWhite:0.7 alpha:0.7].CGColor;
	shapeLayer.fillColor = UIColor.whiteColor.CGColor;
	shapeLayer.lineWidth = 0.5;
	
	if (currentShapeLayer) {
		[self.layer replaceSublayer:currentShapeLayer with:shapeLayer];
	} else {
		[self.layer insertSublayer:shapeLayer atIndex:0];
	}
}

-(CGPathRef)createPath {
	CGFloat radius = 36.0;
	UIBezierPath *path = [UIBezierPath new];
	CGFloat centerWidth = self.frame.size.width/2;
  CGFloat borderRadius = 6;
	
	[path moveToPoint:CGPointMake(0, 0)];
	[path addLineToPoint:CGPointMake(centerWidth-radius-borderRadius, 0)];
  [path addArcWithCenter:CGPointMake(centerWidth-radius-borderRadius+5, borderRadius) radius:borderRadius startAngle:[self degreeToRadians:-90]
                endAngle:[self degreeToRadians:20] clockwise:true];
	[path addArcWithCenter:CGPointMake(centerWidth, 49-radius+ 12) radius:radius startAngle:[self degreeToRadians:180+15]
				  endAngle:[self degreeToRadians:-15] clockwise:false];
  [path addArcWithCenter:CGPointMake(centerWidth+radius+borderRadius-5, borderRadius) radius:borderRadius startAngle:[self degreeToRadians:180-20]
                endAngle:[self degreeToRadians:270] clockwise:true];
	[path addLineToPoint:CGPointMake(self.frame.size.width, 0)];
	[path addLineToPoint:CGPointMake(self.frame.size.width, self.frame.size.height)];
	[path addLineToPoint:CGPointMake(0, self.frame.size.height)];
	[path closePath];
	
	return [path CGPath];
}

-(CGFloat)degreeToRadians:(CGFloat)degree {
	return degree * M_PI / 180;
}

@end
