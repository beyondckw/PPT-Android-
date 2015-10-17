package com.newppt.android.logical;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.newppt.android.entity.MyPath;

public class FileInfo {

	/**
	 * �����ļ���
	 * 
	 * @param savePath
	 */
	public static void CreateFile(String savePath) {
		File file = new File(savePath);

		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
	}
	
	public static boolean fileExist(String savePath) {
		File file = new File(savePath);
		return file.exists();
	}

	/**
	 * ��ȡ·��������JPG����Ϣ
	 * 
	 * @param path
	 * @return
	 */
	public static List<JpgPathInfo> getJpgPathInfos(String path) {
		File pptFile = new File(path + MyPath.pptJpg);
		List<JpgPathInfo> infos = new ArrayList<JpgPathInfo>();
		JpgPathInfo info;

		String[] fileNames = pptFile.list();

		for (String name : fileNames) {
			info = new JpgPathInfo();
			info.setPptJpg(path + MyPath.pptJpg + "/" + name);
			File noteFile = new File(path + MyPath.noteJpg + "/" + name);
			if (noteFile.exists()) {
				info.setNoteJpg(path + MyPath.noteJpg + "/" + name);
			}

			infos.add(info);

		}

		return infos;
	}

	/**
	 * ����·��ɾ��ָ����Ŀ¼���ļ������۴������
	 *
	 * @param sPath
	 *            Ҫɾ����Ŀ¼���ļ�
	 * @return ɾ���ɹ����� true�����򷵻� false��
	 */
	public static boolean DeleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// �ж�Ŀ¼���ļ��Ƿ����
		if (!file.exists()) { // �����ڷ��� false
			return flag;
		} else {
			// �ж��Ƿ�Ϊ�ļ�
			if (file.isFile()) { // Ϊ�ļ�ʱ����ɾ���ļ�����
				return deleteFile(sPath);
			} else { // ΪĿ¼ʱ����ɾ��Ŀ¼����
				return deleteDirectory(sPath);
			}
		}
	}

	/**
	 * ɾ�������ļ�
	 * 
	 * @param sPath
	 *            ��ɾ���ļ����ļ���
	 * @return �����ļ�ɾ���ɹ�����true�����򷵻�false
	 */
	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// ·��Ϊ�ļ��Ҳ�Ϊ�������ɾ��
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * ɾ��Ŀ¼���ļ��У��Լ�Ŀ¼�µ��ļ�
	 * 
	 * @param sPath
	 *            ��ɾ��Ŀ¼���ļ�·��
	 * @return Ŀ¼ɾ���ɹ�����true�����򷵻�false
	 */
	public static boolean deleteDirectory(String sPath) {
		// ���sPath�����ļ��ָ�����β���Զ�����ļ��ָ���
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// ���dir��Ӧ���ļ������ڣ����߲���һ��Ŀ¼�����˳�
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// ɾ���ļ����µ������ļ�(������Ŀ¼)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// ɾ�����ļ�
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // ɾ����Ŀ¼
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// ɾ����ǰĿ¼
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ��ȡ�ļ��޸�ʱ��
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static String getTime(String path) throws Exception {
		File file = new File(path);
		long modifiedTime = file.lastModified();
		Date date = new Date(modifiedTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM");
		String time = sdf.format(date);
		return time;
	}

	public static Map<String, Object> loadPPTInfo(String path) throws Exception {
		File file = new File(path);

		List<String> pptList = new ArrayList<String>();
		List<String> timeList = new ArrayList<String>();
		Map<String, Object> map = new HashMap<String, Object>();

		String[] fileNames = file.list();
		for (String name : fileNames) {
			if (name.endsWith(".ppt") || name.endsWith(".pptx")) {
				pptList.add(name);
				timeList.add(getTime(path + name));
			}
		}
		map.put("pptList", pptList);
		map.put("timeList", timeList);
		return map;
	}
}
