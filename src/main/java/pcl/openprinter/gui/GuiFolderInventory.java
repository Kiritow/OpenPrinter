package pcl.openprinter.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.items.FolderContainer;
import pcl.openprinter.items.FolderInventory;
import pcl.openprinter.network.MessageGUIFolder;
import pcl.openprinter.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class GuiFolderInventory extends GuiContainer {
	/** x and y size of the inventory window in pixels. Defined as float, passed as int
	 * These are used for drawing the player model. */
	private float xSize_lo;
	private float ySize_lo;
	private GuiTextField text;
	private String name;

	private static final ResourceLocation iconLocation = new ResourceLocation("openprinter", "textures/gui/inventoryitem.png");

	/** The inventory to render on screen */
	private final FolderInventory inventory;

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		this.text = new GuiTextField(1, this.fontRenderer, this.width / 2 - 68, this.height/2-78, 137, 10);
		text.setMaxStringLength(203);
		text.setText("Name");
		String s = this.inventory.hasCustomName() ? this.inventory.getName() : I18n.translateToLocal(this.inventory.getName());
		//this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		this.text.setText(s);
		this.text.setFocused(true);
	}

	public GuiFolderInventory(FolderContainer containerItem)
	{
		super(containerItem);
		this.inventory = containerItem.inventory;
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		this.text.updateCursorCounter();
	}

	@Override
	protected void keyTyped(char key, int par2)
	{       
		//super.keyTyped(key, par2);

		if (text.isFocused()) {
			text.textboxKeyTyped(key, par2);
			//nameString = name.getText();
		}

		if (par2 == 1) {
			this.mc.player.closeScreen();
		}

		if (key == '\r') {
			this.name = this.text.getText();
			actionPerformed();
		}
	}
	@Override
	protected void mouseClicked(int x, int y, int btn) throws IOException {
		try {
			super.mouseClicked(x, y, btn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.text.mouseClicked(x, y, btn);
	}

	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
		this.inventory.setInventoryName(this.text.getText());
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		this.xSize_lo = (float)par1;
		this.ySize_lo = (float)par2;

	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		//String s = this.inventory.hasCustomInventoryName() ? this.inventory.getInventoryName() : StatCollector.translateToLocal(this.inventory.getInventoryName());
		//this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		//this.text.setText(s);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(iconLocation);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		int i1;
		this.text.drawTextBox();
		//drawPlayerModel(k + 51, l + 75, 30, (float)(k + 51) - this.xSize_lo, (float)(l + 75 - 50) - this.ySize_lo, this.mc.thePlayer);
	}

	@SideOnly(Side.CLIENT)
	protected void actionPerformed() {
		this.name = this.text.getText();
		PacketHandler.INSTANCE.sendToServer(new MessageGUIFolder(this.name, 1));

	}

	/**
	 * This renders the player model in standard inventory position (in later versions of Minecraft / Forge, you can
	 * simply call GuiInventory.drawEntityOnScreen directly instead of copying this code)
	 */
	public static void drawPlayerModel(int x, int y, int scale, float yaw, float pitch, EntityLivingBase entity) {
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 50.0F);
		GL11.glScalef(-scale, scale, scale);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		float f2 = entity.renderYawOffset;
		float f3 = entity.rotationYaw;
		float f4 = entity.rotationPitch;
		float f5 = entity.prevRotationYawHead;
		float f6 = entity.rotationYawHead;
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float) Math.atan(pitch / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
		entity.renderYawOffset = (float) Math.atan(yaw / 40.0F) * 20.0F;
		entity.rotationYaw = (float) Math.atan(yaw / 40.0F) * 40.0F;
		entity.rotationPitch = -((float) Math.atan(pitch / 40.0F)) * 20.0F;
		entity.rotationYawHead = entity.rotationYaw;
		entity.prevRotationYawHead = entity.rotationYaw;
		GL11.glTranslated(0.0F, entity.getYOffset(), 0.0F);
		Minecraft.getMinecraft().getRenderManager().playerViewY = 180.0F;
		Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity).doRender(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		entity.renderYawOffset = f2;
		entity.rotationYaw = f3;
		entity.rotationPitch = f4;
		entity.prevRotationYawHead = f5;
		entity.rotationYawHead = f6;
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public void setName(String name2) {
		this.name = name2;
	}
}
