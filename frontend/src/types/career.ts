export type Career = {
  id: number;
  title: string;
  description?: string | null;
  sourceUrl?: string | null;
  createdAt?: string | null;
};

export type CareerSkill = {
  id: number;
  careerId: number;
  skillName: string;
};

export type CareerRequirement = {
  id: number;
  careerId: number;
  chunkIndex: number;
  requirementText: string;
  hasEmbedding: boolean;
  createdAt?: string | null;
};

export type CareerSearchResult = {
  requirementId: number;
  careerId: number;
  careerTitle: string;
  chunkIndex: number;
  requirementText: string;
  similarity: number;
  createdAt?: string | null;
};
