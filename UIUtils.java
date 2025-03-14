import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class UIUtils {
    public static JButton createTransparentButtonWithIcon(String text, String iconPath, int fontSize) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;
            
            {
                // Initialize mouse listeners
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        isHovered = true;
                        repaint();
                    }
                    
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        isHovered = false;
                        isPressed = false;
                        repaint();
                    }
                    
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        isPressed = true;
                        repaint();
                    }
                    
                    public void mouseReleased(java.awt.event.MouseEvent evt) {
                        isPressed = false;
                        repaint();
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                Color bgColor;
                
                if (!isEnabled()) {
                    bgColor = new Color(50, 50, 50, 150);
                } else if (isPressed) {
                    bgColor = new Color(30, 30, 30, 240);
                } else if (isHovered) {
                    bgColor = new Color(60, 60, 60, 240);
                } else {
                    bgColor = new Color(40, 40, 40, 220);
                }
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                if (isEnabled() && isHovered && !isPressed) {
                    g2d.setColor(new Color(100, 100, 255, 40));
                    g2d.setStroke(new java.awt.BasicStroke(3f));
                    g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 15, 15);
                } else if (!isEnabled()) {
                    g2d.setColor(new Color(70, 70, 70, 80));
                    g2d.setStroke(new java.awt.BasicStroke(1.5f));
                    g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 15, 15);
                }
                
                ImageIcon icon = (ImageIcon) getIcon();
                FontMetrics fm = g2d.getFontMetrics();
                int iconTextGap = getIconTextGap();
                int iconWidth = icon != null ? icon.getIconWidth() : 0;
                
                int offsetX = (isEnabled() && isPressed) ? 2 : 0;
                int offsetY = (isEnabled() && isPressed) ? 2 : 0;
                
                int x = icon != null ? 20 + offsetX : 10 + offsetX;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent() + offsetY;
                
                if (icon != null) {
                    int iconY = (getHeight() - icon.getIconHeight()) / 2 + offsetY;
                    
                    if (!isEnabled()) {
                        java.awt.AlphaComposite alphaComposite = java.awt.AlphaComposite.getInstance(
                            java.awt.AlphaComposite.SRC_OVER, 0.5f);
                        g2d.setComposite(alphaComposite);
                        icon.paintIcon(this, g2d, x, iconY);
                        g2d.setComposite(java.awt.AlphaComposite.SrcOver);
                    } else {
                        icon.paintIcon(this, g2d, x, iconY);
                    }
                    
                    x += iconWidth + iconTextGap;
                }
                
                if (isEnabled()) {
                    g2d.setColor(new Color(0, 0, 0, 180));
                } else {
                    g2d.setColor(new Color(0, 0, 0, 100));
                }
                g2d.drawString(getText(), x + 2, textY + 2);
                
                Color textColor;
                if (!isEnabled()) {
                    textColor = new Color(160, 160, 160);
                } else if (isHovered) {
                    textColor = isPressed ? new Color(200, 200, 200) : Color.YELLOW;
                } else {
                    textColor = Color.WHITE;
                }
                g2d.setColor(textColor);
                g2d.drawString(getText(), x, textY);
                
                g2d.dispose();
            }
        };
        
        button.setFont(new Font("Courier New", Font.BOLD, fontSize));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setOpaque(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
		if (iconPath == null) {
			button.setIconTextGap(20);
		} else {
			try {
				Image img = ImageIO.read(new File(iconPath));
				Image resizedImg = img.getScaledInstance(36, 36, Image.SCALE_SMOOTH);
				button.setIcon(new ImageIcon(resizedImg));
				
				button.setHorizontalTextPosition(JButton.RIGHT);
				button.setIconTextGap(12);
			} catch (IOException e) {
				System.err.println("Could not load icon: " + iconPath);
			}
		}
        
        return button;
    }

	public static JButton createTransparentButtonWithIcon(String text, String iconPath) {
		return createTransparentButtonWithIcon(text, iconPath, 28);
	}

	public static JButton createTransparentButton(String text, int fontSize) {
		return createTransparentButtonWithIcon(text, null, fontSize);
	}

	public static JButton createTransparentButton(String text) {
		return createTransparentButtonWithIcon(text, null);
	}
}