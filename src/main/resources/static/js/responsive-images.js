/**
 * Responsive Images JavaScript - PageSpeed optimization
 * Handles lazy loading completion and improves Core Web Vitals
 */

document.addEventListener('DOMContentLoaded', function() {
    // Mark lazy loaded images as loaded to remove loading placeholder
    const lazyImages = document.querySelectorAll('img[loading="lazy"]');
    
    lazyImages.forEach(img => {
        // If image is already loaded
        if (img.complete) {
            img.classList.add('loaded');
        }
        
        // Listen for load event
        img.addEventListener('load', function() {
            this.classList.add('loaded');
        });
        
        // Handle loading errors
        img.addEventListener('error', function() {
            this.classList.add('loaded', 'error');
            console.warn('Failed to load image:', this.src);
        });
    });
    
    // Intersection Observer for better lazy loading control
    if ('IntersectionObserver' in window) {
        const imageObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    
                    // Preload higher resolution variant for retina displays
                    if (window.devicePixelRatio > 1 && img.srcset) {
                        const srcsetUrls = img.srcset.split(',');
                        const highestRes = srcsetUrls[srcsetUrls.length - 1].trim().split(' ')[0];
                        
                        // Preload the highest resolution image
                        const preloadImg = new Image();
                        preloadImg.src = highestRes;
                    }
                    
                    observer.unobserve(img);
                }
            });
        }, {
            rootMargin: '50px 0px', // Start loading 50px before image comes into view
            threshold: 0.01
        });
        
        // Observe all responsive images
        document.querySelectorAll('img[srcset]').forEach(img => {
            imageObserver.observe(img);
        });
    }
    
    // Add performance monitoring for Core Web Vitals
    if ('PerformanceObserver' in window) {
        // Monitor Largest Contentful Paint (LCP)
        new PerformanceObserver((entryList) => {
            const entries = entryList.getEntries();
            const lastEntry = entries[entries.length - 1];
            
            // Log LCP if it's an image
            if (lastEntry.element && lastEntry.element.tagName === 'IMG') {
                console.log('LCP Image:', lastEntry.element.src, 'Time:', lastEntry.startTime);
            }
        }).observe({ entryTypes: ['largest-contentful-paint'] });
        
        // Monitor Cumulative Layout Shift (CLS)
        new PerformanceObserver((entryList) => {
            let clsValue = 0;
            for (const entry of entryList.getEntries()) {
                if (!entry.hadRecentInput) {
                    clsValue += entry.value;
                }
            }
            if (clsValue > 0.1) {
                console.warn('High CLS detected:', clsValue);
            }
        }).observe({ entryTypes: ['layout-shift'] });
    }
});

// Utility function to get optimal image size based on container
function getOptimalImageSize(container) {
    const containerWidth = container.offsetWidth;
    const dpr = window.devicePixelRatio || 1;
    const targetWidth = containerWidth * dpr;
    
    // Return closest responsive breakpoint
    if (targetWidth <= 400) return 400;
    if (targetWidth <= 800) return 800;
    return 1200;
}

// Export for use in other scripts
window.ResponsiveImages = {
    getOptimalImageSize: getOptimalImageSize
};