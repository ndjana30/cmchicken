package com.limiter.demo.subscription;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public enum Subscription {
    Basic {
        public ResponseEntity<Object> getPlan() {
            return new ResponseEntity<>("Basic Plan", HttpStatus.OK);
        }
    },
    MEDIUM {
        public ResponseEntity<Object> getPlan() {
            return new ResponseEntity<>("Medium Plan", HttpStatus.OK);
        }
    },
    PROFESSIONAL {
        public ResponseEntity<Object> getPlan() {
            return new ResponseEntity<>("Professional Plan", HttpStatus.OK);
        }
    }
}


